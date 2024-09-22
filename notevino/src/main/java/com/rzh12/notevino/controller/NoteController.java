package com.rzh12.notevino.controller;

import com.rzh12.notevino.dto.ApiResponse;
import com.rzh12.notevino.dto.FreeFormNoteRequest;
import com.rzh12.notevino.dto.WineDetailsResponse;
import com.rzh12.notevino.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wines")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @PostMapping("/{wineId}/notes")
    public ResponseEntity<ApiResponse> createTastingNote(
            @PathVariable Integer wineId,
            @RequestBody FreeFormNoteRequest freeFormNoteRequest) {

        boolean noteCreated = noteService.createFreeFormNote(wineId, freeFormNoteRequest);

        if (noteCreated) {
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, "Note created successfully!", null));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, "Failed to create note!", null));
        }
    }

    // 取得特定葡萄酒的詳細資訊和相關筆記
    @GetMapping("/{wineId}")
    public ResponseEntity<ApiResponse> getWineDetailsWithNotes(@PathVariable Integer wineId) {
        WineDetailsResponse wineDetails = noteService.getWineDetailsWithNotes(wineId);

        if (wineDetails != null) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Wine details retrieved successfully!", wineDetails));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "Wine not found!", null));
        }
    }
}
