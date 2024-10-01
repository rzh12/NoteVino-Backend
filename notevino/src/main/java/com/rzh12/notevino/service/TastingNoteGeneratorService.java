package com.rzh12.notevino.service;

import com.rzh12.notevino.client.OpenAIClient;
import com.rzh12.notevino.dto.WineDetailsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TastingNoteGeneratorService {

    @Autowired
    private OpenAIClient openAIClient;

    @Autowired
    private NoteService noteService;

    /**
     * 根據葡萄酒的詳細資訊生成 Tasting Note 範例
     * @param wineId 葡萄酒的ID
     * @return 生成的 Tasting Note 範例
     */
    public String generateTastingNoteExample(Integer wineId) {
        // 使用 getWineDetailsWithNotes 取得葡萄酒詳細資訊
        WineDetailsResponse wineDetails = noteService.getWineDetailsWithNotes(wineId);

        // 構建 OpenAI API 所需的提示詞
        String systemPrompt = "You are a professional sommelier. Based on the following wine information, generate a sample tasting note that could help a beginner learn how to write one.";
        String userQuery = "Here is the wine information: \n"
                + "Name: " + wineDetails.getName() + "\n"
                + "Region: " + wineDetails.getRegion() + "\n"
                + "Type: " + wineDetails.getType() + "\n"
                + "Vintage: " + (wineDetails.getVintage() != null ? wineDetails.getVintage() : "Non-vintage") + "\n";

        String assistantMessageContent = "Please generate a sample tasting note based on the wine information above. The note should focus on flavor, aroma, texture, and any other characteristics typically found in professional tasting notes.";

        // 調用 OpenAIClient 來生成 Tasting Note 範例
        return openAIClient.generateResponse(systemPrompt, userQuery, assistantMessageContent);
    }
}
