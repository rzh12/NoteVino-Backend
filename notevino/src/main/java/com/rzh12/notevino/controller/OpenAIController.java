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
     * 生成一個 Tasting Note 範例
     * @param wineId 在 Header 中傳入的葡萄酒ID
     * @return 生成的 Tasting Note
     */
    @GetMapping("/api/wines/generate-tasting-note")
    public ResponseEntity<String> generateTastingNote(@RequestHeader("wineId") Integer wineId) {
        // 調用 service 來生成 Tasting Note
        String tastingNote = tastingNoteGeneratorService.generateTastingNoteExample(wineId);

        // 返回結果
        if (tastingNote != null && !tastingNote.isEmpty()) {
            return ResponseEntity.ok(tastingNote);
        } else {
            return ResponseEntity.noContent().build();  // 如果無法生成則返回 204 No Content
        }
    }
}
