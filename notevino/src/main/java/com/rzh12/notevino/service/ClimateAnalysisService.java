package com.rzh12.notevino.service;

import com.rzh12.notevino.client.OpenAIClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClimateAnalysisService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private OpenAIClient openAIClient;

    /**
     * Retrieve the names and region data of all wines based on the user ID, and return the climate code.
     * @param userId The ID of the user
     * @return The corresponding climate code value; if no valid climate type can be extracted, return 3 (Maritime Climate)
     */
    public int analyzeFavoriteClimateByUserId(Integer userId) {
        // 1. Query all wine names and regions uploaded by the user
        String sql = "SELECT name, region FROM user_uploaded_wines WHERE user_id = ? AND is_deleted = 0";
        List<Map<String, Object>> wines = jdbcTemplate.queryForList(sql, userId);

        if (wines.isEmpty()) {
            return 3;
        }

        // 2. Construct a string containing the names and regions as the basis for analysis
        String wineData = wines.stream()
                .map(wine -> wine.get("name") + " (" + wine.get("region") + ")")
                .collect(Collectors.joining(", "));

        // 3. Set the prompt and call OpenAI for climate analysis
        String systemPrompt = "You are a professional wine climate analysis assistant. Based on the wine names and regions provided, please select one of the following six climate types, and only return that single word: 'Cool Climate', 'Warm Climate', 'Mediterranean Climate', 'High Altitude', 'Maritime Climate', 'Continental Climate'. Do not return any additional text.";
        String userQuery = "Here are the wine names and regions uploaded by this user: " + wineData;
        String assistantMessageContent = "Based on this information, choose one of the six climate types, return only that single word, and do not provide any additional content.";

        String openAIResponse = openAIClient.generateResponse(systemPrompt, userQuery, assistantMessageContent);

        // 4. Call an internal function to convert the OpenAI response into a climate code
        return convertClimateToEncoding(openAIResponse);
    }

    /**
     * Convert the climate type from OpenAI's response into the corresponding code value.
     * @param climateResponse The climate type from OpenAI's response (may contain extra text)
     * @return The corresponding climate code; if none of the six options can be extracted, return 3 (Maritime Climate)
     */
    private int convertClimateToEncoding(String climateResponse) {
        String cleanedResponse = climateResponse.trim().toLowerCase();

        if (cleanedResponse.contains("cool climate")) {
            return 1;
        } else if (cleanedResponse.contains("high altitude")) {
            return 2;
        } else if (cleanedResponse.contains("maritime climate")) {
            return 3;
        } else if (cleanedResponse.contains("continental climate")) {
            return 4;
        } else if (cleanedResponse.contains("mediterranean climate")) {
            return 5;
        } else if (cleanedResponse.contains("warm climate")) {
            return 6;
        }

        return 3;
    }
}
