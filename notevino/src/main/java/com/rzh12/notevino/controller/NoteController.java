package com.rzh12.notevino.controller;

import com.rzh12.notevino.dto.ApiResponse;
import com.rzh12.notevino.dto.FreeFormNoteRequest;
import com.rzh12.notevino.dto.FreeFormNoteResponse;
import com.rzh12.notevino.dto.WineDetailsResponse;
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Failed to create note!", null));
        }
    }

    // Get detailed information on a particular wine and the associated notes
    @GetMapping("/{wineId}")
    public ResponseEntity<ApiResponse> getWineDetailsWithNotes(@PathVariable Integer wineId) {
        WineDetailsResponse wineDetails = noteService.getWineDetailsWithNotes(wineId);

        if (wineDetails != null) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Wine details retrieved successfully!", wineDetails));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "Wine not found!", null));
        }
    }

    @PutMapping("/{wineId}/notes/{noteId}")
    public ResponseEntity<ApiResponse> updateTastingNote(
            @PathVariable Integer wineId,
            @PathVariable Integer noteId,
            @RequestBody FreeFormNoteRequest freeFormNoteRequest) {

        try {
            LocalDateTime updatedAt = noteService.updateFreeFormNote(wineId, noteId, freeFormNoteRequest);

            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, "Note updated successfully!", updatedAt));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "An error occurred while updating the note.", null));
        }
    }

    @DeleteMapping("/{wineId}/notes/{noteId}")
    public ResponseEntity<ApiResponse> deleteTastingNote(
            @PathVariable Integer wineId,
            @PathVariable Integer noteId) {

        boolean noteDeleted = noteService.deleteNote(wineId, noteId);

        if (noteDeleted) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "Wine or Note not found!", null));
        }
    }
}
