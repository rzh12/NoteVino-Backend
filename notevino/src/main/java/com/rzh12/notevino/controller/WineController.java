package com.rzh12.notevino.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rzh12.notevino.dto.ApiResponse;
import com.rzh12.notevino.dto.WineAutocompleteResponse;
import com.rzh12.notevino.dto.WineRequest;
import com.rzh12.notevino.dto.WineResponse;
import com.rzh12.notevino.service.WineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/wines")
public class WineController {

    @Autowired
    private WineService wineService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> uploadWine(
            @RequestParam("info") String wineDataString,
            @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        WineRequest wineRequest = objectMapper.readValue(wineDataString, WineRequest.class);

        Integer wineId = wineService.addNewWine(wineRequest, image);

        // 上傳成功後，增加該酒款在 Redis 中的頻次
        wineService.incrementWineScore(wineRequest.getName(), wineRequest.getRegion());

        // 返回成功響應
        ApiResponse response = new ApiResponse(true, "Wine added successfully!", wineId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse> getWineList() {
        List<WineResponse> wineList = wineService.getUserUploadedWines();

        if (wineList.isEmpty()) {
            ApiResponse<List<WineResponse>> response = new ApiResponse<>(true, "No wines found.", null);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }

        ApiResponse response = new ApiResponse(true, "Wines retrieved successfully!", wineList);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{wineId}")
    public ResponseEntity<ApiResponse> editWine(
            @PathVariable Integer wineId,
            @RequestParam("info") String wineDataString) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        WineRequest wineRequest = objectMapper.readValue(wineDataString, WineRequest.class);

        boolean updated = wineService.updateWine(wineId, wineRequest);

        if (updated) {
            ApiResponse response = new ApiResponse(true, "Wine updated successfully!", null);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "Wine not found!", null));
        }
    }

    @DeleteMapping("/{wineId}")
    public ResponseEntity<ApiResponse> deleteWine(
            @PathVariable Integer wineId) {

        boolean deleted = wineService.deleteWine(wineId);

        if (deleted) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "Wine not found or unauthorized!", null));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchWines(@RequestParam("query") String query) {
        List<WineResponse> searchResults = wineService.searchWinesByName(query);

        if (searchResults.isEmpty()) {
            ApiResponse<List<WineResponse>> response = new ApiResponse<>(true, "No wines found.", null);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }

        ApiResponse response = new ApiResponse(true, "Wines retrieved successfully!", searchResults);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/autocomplete")
    public ResponseEntity<ApiResponse<List<WineAutocompleteResponse>>> autocompleteWines(
            @RequestParam("query") String query) {

        // 調用服務層獲取自動完成的匹配結果
        List<WineAutocompleteResponse> results = wineService.autocompleteWines(query);

        if (!results.isEmpty()) {
            // 返回自動完成的匹配結果
            return ResponseEntity.ok(new ApiResponse<>(true, "Matches found!", results));
        } else {
            // 如果沒有匹配結果，返回 204 No Content
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ApiResponse<>(false, "No matches found!", null));
        }
    }

}
