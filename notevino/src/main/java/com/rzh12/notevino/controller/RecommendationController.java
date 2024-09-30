package com.rzh12.notevino.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rzh12.notevino.service.KnnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wines")
public class RecommendationController {

    @Autowired
    private KnnService knnService;

    @GetMapping("/{wineId}/recommendations")
    public ResponseEntity<List<Map<String, Object>>> recommendWines(
            @PathVariable Long wineId,
            @RequestHeader(value = "rating", defaultValue = "4.3") double rating,
            @RequestHeader(value = "price", defaultValue = "5000") double price) throws IOException {

        // 調用 knnService 來進行推薦，並獲取推薦的 JSON 字串
        String recommendations = knnService.recommendWinesByWineId(wineId, rating, price);

        // 如果沒有推薦結果
        if (recommendations == null || recommendations.isEmpty()) {
            return ResponseEntity.noContent().build();  // 204 No Content
        }

        // 使用 ObjectMapper 將推薦結果的 JSON 字串轉換為 List<Map<String, Object>>
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> recommendationList = objectMapper.readValue(recommendations, new TypeReference<List<Map<String, Object>>>() {});

        // 返回 JSON 格式的推薦結果
        return ResponseEntity.ok(recommendationList);  // 200 OK
    }

    // 根據使用者進行推薦
    @GetMapping("/user/recommendations")
    public ResponseEntity<List<Map<String, Object>>> recommendWinesByUser(
            @RequestHeader(value = "rating", defaultValue = "4.3") double rating,
            @RequestHeader(value = "price", defaultValue = "5000") double price) throws IOException {

        // 調用 knnService 來進行根據 userId 推薦
        String recommendations = knnService.recommendWinesByCurrentUser(rating, price);

        if (recommendations == null || recommendations.isEmpty()) {
            return ResponseEntity.noContent().build();  // 204 No Content
        }

        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> recommendationList = objectMapper.readValue(recommendations, new TypeReference<List<Map<String, Object>>>() {});

        return ResponseEntity.ok(recommendationList);  // 200 OK
    }
}
