package com.rzh12.notevino.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rzh12.notevino.dto.ApiResponse;
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
            @RequestParam("info") String wineDataString,  // 接收 JSON 格式的資料
            @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

        // 使用 ObjectMapper 解析 JSON 字符串
        ObjectMapper objectMapper = new ObjectMapper();
        WineRequest wineRequest = objectMapper.readValue(wineDataString, WineRequest.class);

        // 調用服務層處理數據
        wineService.addNewWine(wineRequest, image);

        // 假設數據成功儲存
        ApiResponse response = new ApiResponse(true, "Wine added successfully!", null);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse> getWineList() {
        // 調用服務層取得當前用戶上傳的葡萄酒列表
        List<WineResponse> wineList = wineService.getUserUploadedWines();

        // 判斷是否有內容
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

        // 使用 ObjectMapper 解析 JSON 字符串
        ObjectMapper objectMapper = new ObjectMapper();
        WineRequest wineRequest = objectMapper.readValue(wineDataString, WineRequest.class);

        // 調用服務層處理數據
        boolean updated = wineService.updateWine(wineId, wineRequest);

        // 判斷是否成功更新
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

    // 搜尋葡萄酒根據名稱
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

}
