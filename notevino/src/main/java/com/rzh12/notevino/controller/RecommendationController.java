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

    @GetMapping("/user/recommendations")
    public ResponseEntity<List<Map<String, Object>>> recommendWinesByUser(
            @RequestHeader(value = "rating", defaultValue = "4.3") double rating,
            @RequestHeader(value = "price", defaultValue = "5000") double price,
            @RequestHeader(value = "useRegion", defaultValue = "false") boolean useRegion,
            @RequestHeader(value = "region", required = false) String region) throws IOException {

        // Call the knnService to make recommendations based on userId, and decide whether to use the region passed from the frontend
        String recommendations = knnService.recommendWinesByCurrentUser(rating, price, useRegion, region);

        if (recommendations == null || recommendations.isEmpty()) {
            return ResponseEntity.noContent().build();  // 204 No Content
        }

        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> recommendationList = objectMapper.readValue(recommendations, new TypeReference<List<Map<String, Object>>>() {});

        return ResponseEntity.ok(recommendationList);  // 200 OK
    }
}
