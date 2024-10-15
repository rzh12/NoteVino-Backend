package com.rzh12.notevino.controller;

import com.rzh12.notevino.dto.ApiResponse;
import com.rzh12.notevino.dto.SatNoteRequest;
import com.rzh12.notevino.dto.SatNoteResponse;
import com.rzh12.notevino.service.SatNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wines")
public class SatNoteController {

    @Autowired
    private SatNoteService satNoteService;

    // Create SAT Note
    @PostMapping("/{wineId}/sat-note")
    public ResponseEntity<ApiResponse> createSatNote(
            @PathVariable Integer wineId,
            @RequestBody SatNoteRequest satNoteRequest) {

        try {
            SatNoteResponse satNoteResponse = satNoteService.createSatNote(wineId, satNoteRequest);

            ApiResponse<SatNoteResponse> response = new ApiResponse<>(true, "SAT note created successfully!", satNoteResponse);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            ApiResponse<String> response = new ApiResponse<>(false, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);

        } catch (RuntimeException e) {
            ApiResponse<String> response = new ApiResponse<>(false, "Failed to create SAT note.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(false, "An unexpected error occurred.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Read SAT Note
    @GetMapping("/{wineId}/sat-note")
    public ResponseEntity<ApiResponse> getSatNote(@PathVariable Integer wineId) {
        try {
            SatNoteResponse satNoteResponse = satNoteService.getSatNoteByWineId(wineId);
            if (satNoteResponse == null) {
                // Return 200 and null SAT note
                return ResponseEntity.ok(new ApiResponse<>(true, "No SAT note found", null));
            }

            ApiResponse<SatNoteResponse> response = new ApiResponse<>(true, "SAT note retrieved successfully!", satNoteResponse);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (RuntimeException e) {
            ApiResponse<String> response = new ApiResponse<>(false, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // Update SAT Note
    @PutMapping("/{wineId}/sat-note")
    public ResponseEntity<ApiResponse> updateSatNote(
            @PathVariable Integer wineId,
            @RequestBody SatNoteRequest satNoteRequest) {

        try {
            boolean updated = satNoteService.updateSatNote(wineId, satNoteRequest);

            if (updated) {
                ApiResponse<String> response = new ApiResponse<>(true, "SAT note updated successfully!", null);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                ApiResponse<String> response = new ApiResponse<>(false, "SAT note not found or unauthorized!", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (IllegalArgumentException e) {
            ApiResponse<String> response = new ApiResponse<>(false, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);

        } catch (RuntimeException e) {
            ApiResponse<String> response = new ApiResponse<>(false, "Failed to update SAT note.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(false, "An unexpected error occurred.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
