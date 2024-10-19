package com.rzh12.notevino.controller;

import com.rzh12.notevino.dto.ApiResponse;
import com.rzh12.notevino.dto.FreeFormNoteRequest;
import com.rzh12.notevino.dto.FreeFormNoteResponse;
import com.rzh12.notevino.dto.WineDetailsResponse;
import com.rzh12.notevino.exception.ResourceNotFoundException;
import com.rzh12.notevino.exception.BadRequestException;
import com.rzh12.notevino.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/wines")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @PostMapping("/{wineId}/notes")
    public ResponseEntity<ApiResponse<FreeFormNoteResponse>> createTastingNote(
            @PathVariable Integer wineId,
            @RequestBody FreeFormNoteRequest freeFormNoteRequest) {

        FreeFormNoteResponse noteResponse = noteService.createFreeFormNote(wineId, freeFormNoteRequest);

        if (noteResponse != null) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Note created successfully!", noteResponse));
        } else {
            throw new BadRequestException("Failed to create note!");
        }
    }

    @GetMapping("/{wineId}")
    public ResponseEntity<ApiResponse> getWineDetailsWithNotes(@PathVariable Integer wineId) {
        WineDetailsResponse wineDetails = noteService.getWineDetailsWithNotes(wineId);

        if (wineDetails != null) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Wine details retrieved successfully!", wineDetails));
        } else {
            throw new ResourceNotFoundException("Wine not found!");
        }
    }

    @PutMapping("/{wineId}/notes/{noteId}")
    public ResponseEntity<ApiResponse> updateTastingNote(
            @PathVariable Integer wineId,
            @PathVariable Integer noteId,
            @RequestBody FreeFormNoteRequest freeFormNoteRequest) {

        LocalDateTime updatedAt = noteService.updateFreeFormNote(wineId, noteId, freeFormNoteRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, "Note updated successfully!", updatedAt));
    }

    @DeleteMapping("/{wineId}/notes/{noteId}")
    public ResponseEntity<ApiResponse> deleteTastingNote(
            @PathVariable Integer wineId,
            @PathVariable Integer noteId) {

        boolean noteDeleted = noteService.deleteNote(wineId, noteId);

        if (noteDeleted) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            throw new ResourceNotFoundException("Wine or Note not found!");
        }
    }
}
