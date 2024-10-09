package com.rzh12.notevino.controller;

import com.rzh12.notevino.service.TastingNoteGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OpenAIController {

    @Autowired
    private TastingNoteGeneratorService tastingNoteGeneratorService;

    /**
     * Generate a sample Tasting Note
     * @param wineId The wine ID passed in the Header
     * @return The generated Tasting Note
     */
    @GetMapping("/api/wines/generate-tasting-note")
    public ResponseEntity<String> generateTastingNote(@RequestHeader("wineId") Integer wineId) {

        String tastingNote = tastingNoteGeneratorService.generateTastingNoteExample(wineId);

        if (tastingNote != null && !tastingNote.isEmpty()) {
            return ResponseEntity.ok(tastingNote);
        } else {
            return ResponseEntity.noContent().build();  // 204 No Content
        }
    }
}
