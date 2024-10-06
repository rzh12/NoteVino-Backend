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

    // 創建或更新 SAT Note
    // 創建 SAT Note
    @PostMapping("/{wineId}/sat-note")
    public ResponseEntity<ApiResponse> createSatNote(
            @PathVariable Integer wineId,
            @RequestBody SatNoteRequest satNoteRequest) {

        try {
            // 調用服務層進行創建操作
            SatNoteResponse satNoteResponse = satNoteService.createSatNote(wineId, satNoteRequest);

            // 返回成功的 API 響應
            ApiResponse<SatNoteResponse> response = new ApiResponse<>(true, "SAT note created successfully!", satNoteResponse);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            // 處理無權限的情況
            ApiResponse<String> response = new ApiResponse<>(false, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);

        } catch (RuntimeException e) {
            // 處理 SAT Note 創建失敗的情況
            ApiResponse<String> response = new ApiResponse<>(false, "Failed to create SAT note.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

        } catch (Exception e) {
            // 捕獲其他所有未預期的錯誤
            ApiResponse<String> response = new ApiResponse<>(false, "An unexpected error occurred.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 讀取 SAT Note
    @GetMapping("/{wineId}/sat-note")
    public ResponseEntity<ApiResponse> getSatNote(@PathVariable Integer wineId) {

        // 調用服務層獲取 SAT Note
        SatNoteResponse satNoteResponse = satNoteService.getSatNoteByWineId(wineId);

        if (satNoteResponse != null) {
            // 返回成功的 API 響應
            ApiResponse<SatNoteResponse> response = new ApiResponse<>(true, "SAT note retrieved successfully!", satNoteResponse);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            // 如果沒有找到 SAT Note，返回 404
            ApiResponse<SatNoteResponse> response = new ApiResponse<>(false, "SAT note not found!", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // 更新 SAT Note
    @PutMapping("/{wineId}/sat-note")
    public ResponseEntity<ApiResponse> updateSatNote(
            @PathVariable Integer wineId,
            @RequestBody SatNoteRequest satNoteRequest) {

        try {
            // 調用服務層進行更新操作
            boolean updated = satNoteService.updateSatNote(wineId, satNoteRequest);

            if (updated) {
                // 返回成功的 API 響應
                ApiResponse<String> response = new ApiResponse<>(true, "SAT note updated successfully!", null);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                // 如果更新失敗，返回 404
                ApiResponse<String> response = new ApiResponse<>(false, "SAT note not found or unauthorized!", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (IllegalArgumentException e) {
            // 處理無權限的情況
            ApiResponse<String> response = new ApiResponse<>(false, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);

        } catch (RuntimeException e) {
            // 處理 SAT Note 更新失敗的情況
            ApiResponse<String> response = new ApiResponse<>(false, "Failed to update SAT note.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

        } catch (Exception e) {
            // 捕獲其他所有未預期的錯誤
            ApiResponse<String> response = new ApiResponse<>(false, "An unexpected error occurred.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
