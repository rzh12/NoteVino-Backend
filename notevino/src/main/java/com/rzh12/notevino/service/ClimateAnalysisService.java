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
     * 根據用戶ID獲取所有葡萄酒的名稱和地區資料，並返回氣候編碼
     * @param userId 用戶的ID
     * @return 對應的氣候編碼值，若無法提取到有效的氣候類型則返回 3 (Maritime Climate)
     */
    public int analyzeFavoriteClimateByUserId(Integer userId) {
        // 1. 查詢用戶上傳的所有葡萄酒名稱和地區
        String sql = "SELECT name, region FROM user_uploaded_wines WHERE user_id = ? AND is_deleted = 0";
        List<Map<String, Object>> wines = jdbcTemplate.queryForList(sql, userId);

        // 如果沒有數據，返回預設值 3 (Maritime Climate)
        if (wines.isEmpty()) {
            return 3;
        }

        // 2. 構建包含名稱和地區的字串作為分析基礎
        String wineData = wines.stream()
                .map(wine -> wine.get("name") + " (" + wine.get("region") + ")")
                .collect(Collectors.joining(", "));

        // 3. 設置提示詞並調用 OpenAI 進行氣候分析
        String systemPrompt = "You are a professional wine climate analysis assistant. Based on the wine names and regions provided, please select one of the following six climate types, and only return that single word: 'Cool Climate', 'Warm Climate', 'Mediterranean Climate', 'High Altitude', 'Maritime Climate', 'Continental Climate'. Do not return any additional text.";
        String userQuery = "Here are the wine names and regions uploaded by this user: " + wineData;
        String assistantMessageContent = "Based on this information, choose one of the six climate types, return only that single word, and do not provide any additional content.";

        String openAIResponse = openAIClient.generateResponse(systemPrompt, userQuery, assistantMessageContent);

        // 4. 調用內部函數將 OpenAI 回應轉換為氣候編碼
        return convertClimateToEncoding(openAIResponse);
    }

    /**
     * 將 OpenAI 回覆的氣候類型轉換為對應的編碼值
     * @param climateResponse OpenAI 回應的氣候類型（可能含有多餘文字）
     * @return 對應的氣候編碼，若無法提取到六個選項中的任何一個，則返回 3 (Maritime Climate)
     */
    private int convertClimateToEncoding(String climateResponse) {
        // 去掉回應中的多餘空白和字元，並轉換為小寫，方便後續比對
        String cleanedResponse = climateResponse.trim().toLowerCase();

        // 依據氣候類型將其轉換為對應的編碼值
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

        // 若無法從回應中提取到有效的氣候類型，預設返回 3 (Maritime Climate)
        return 3;
    }
}
