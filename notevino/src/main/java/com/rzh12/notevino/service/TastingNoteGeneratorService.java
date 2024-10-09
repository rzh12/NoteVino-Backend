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
     * Generate a sample Tasting Note based on the wine details
     * @param wineId The ID of the wine
     * @return The generated sample Tasting Note
     */
    public String generateTastingNoteExample(Integer wineId) {
        // Retrieve wine details using getWineDetailsWithNotes
        WineDetailsResponse wineDetails = noteService.getWineDetailsWithNotes(wineId);

        // Construct the prompt required for the OpenAI API
        String systemPrompt = "You are a professional sommelier. Based on the following wine information, generate a sample tasting note that could help a beginner learn how to write one.";
        String userQuery = "Here is the wine information: \n"
                + "Name: " + wineDetails.getName() + "\n"
                + "Region: " + wineDetails.getRegion() + "\n"
                + "Type: " + wineDetails.getType() + "\n"
                + "Vintage: " + (wineDetails.getVintage() != null ? wineDetails.getVintage() : "Non-vintage") + "\n";

        String assistantMessageContent = "Please generate a sample tasting note based on the wine information above. The note should focus on flavor, aroma, texture, and any other characteristics typically found in professional tasting notes.";

        // Call OpenAIClient to generate a sample Tasting Note
        return openAIClient.generateResponse(systemPrompt, userQuery, assistantMessageContent);
    }
}
