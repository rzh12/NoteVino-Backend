package com.rzh12.notevino.service.impl;

import com.rzh12.notevino.dto.WineAutocompleteResponse;
import com.rzh12.notevino.dto.WineRequest;
import com.rzh12.notevino.dto.WineResponse;
import com.rzh12.notevino.repository.WineRepository;
import com.rzh12.notevino.service.S3Service;
import com.rzh12.notevino.service.UserUtil;
import com.rzh12.notevino.service.WineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WineServiceImpl implements WineService {

    @Autowired
    private WineRepository wineRepository;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Integer addNewWine(WineRequest wineRequest, MultipartFile image) {

        if (image != null && !image.isEmpty()) {
            String imageUrl = s3Service.uploadFile(image, "wine");
            wineRequest.setImageUrl(imageUrl);
        }

        Integer userId = UserUtil.getCurrentUserId();
        wineRequest.setUserId(userId);

        return wineRepository.saveWine(wineRequest);
    }

    @Override
    public List<WineResponse> getUserUploadedWines() {
        Integer userId = UserUtil.getCurrentUserId();
        return wineRepository.findAllByUserId(userId);
    }

    @Override
    public boolean updateWine(Integer wineId, WineRequest wineRequest) {
        Integer userId = UserUtil.getCurrentUserId();

        if (wineRepository.existsByIdAndUserId(wineId, userId)) {
            wineRepository.updateWine(wineId, wineRequest);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteWine(Integer wineId) {
        Integer userId = UserUtil.getCurrentUserId();

        if (wineRepository.existsByIdAndUserId(wineId, userId)) {
            wineRepository.softDeleteWineById(wineId);
            return true;
        }
        return false;
    }

    @Override
    public List<WineResponse> searchWinesByName(String query) {
        Integer userId = UserUtil.getCurrentUserId();
        return wineRepository.searchWinesByNameAndUserId(query, userId);
    }

    @Override
    public List<WineAutocompleteResponse> autocompleteWines(String query) {
        // If the query string is empty, directly display the most popular wines
        if (query == null || query.trim().isEmpty()) {
            // Retrieve the top 10 popular wines from Redis (ZSet)
            Set<Object> cachedResults = redisTemplate.opsForZSet().reverseRange("autocomplete:read", 0, 9);

            if (cachedResults != null && !cachedResults.isEmpty()) {
                return cachedResults.stream()
                        .map(result -> {
                            String[] parts = result.toString().split("\\|");
                            Integer wineId = Integer.parseInt(parts[0]);
                            String wineName = parts[1];
                            String region = parts[2];
                            return new WineAutocompleteResponse(wineId, wineName, region, getScore(wineName, region));
                        })
                        .limit(10)
                        .collect(Collectors.toList());
            } else {
                List<WineAutocompleteResponse> dbResults = wineRepository.getTopWines();

                for (WineAutocompleteResponse result : dbResults) {
                    String redisValue = String.format("%d|%s|%s", result.getWineId(), result.getName(), result.getRegion());
                    redisTemplate.opsForZSet().add("autocomplete:read", redisValue, result.getScore());
                }

                return dbResults;
            }
        }

        Set<Object> cachedResults = redisTemplate.opsForZSet().reverseRange("autocomplete:read", 0, 49);

        String searchQuery = query.toLowerCase();

        List<WineAutocompleteResponse> filteredResults;

        if (cachedResults != null && !cachedResults.isEmpty()) {
            filteredResults = cachedResults.stream()
                    .map(result -> {
                        String[] parts = result.toString().split("\\|");
                        Integer wineId = Integer.parseInt(parts[0]);
                        String wineName = parts[1];
                        String region = parts[2];
                        return new WineAutocompleteResponse(wineId, wineName, region, getScore(wineName, region));
                    })
                    .filter(response -> response.getName().toLowerCase().contains(searchQuery) ||
                            response.getRegion().toLowerCase().contains(searchQuery))
                    .limit(10)
                    .collect(Collectors.toList());
        } else {
            filteredResults = new ArrayList<>();
        }

        // If there are fewer than 10 results in Redis, supplement from the database
        if (filteredResults.size() < 10) {
            int remainingCount = 10 - filteredResults.size();
            List<WineAutocompleteResponse> dbResults = wineRepository.autocompleteWines(query);

            List<WineAutocompleteResponse> additionalResults = dbResults.stream()
                    .filter(response -> filteredResults.stream().noneMatch(existing ->
                            existing.getWineId().equals(response.getWineId())))
                    .limit(remainingCount)
                    .collect(Collectors.toList());

            filteredResults.addAll(additionalResults);

            for (WineAutocompleteResponse result : additionalResults) {
                String redisValue = String.format("%d|%s|%s", result.getWineId(), result.getName(), result.getRegion());
                redisTemplate.opsForZSet().add("autocomplete:read", redisValue, result.getScore());
            }
        }

        return filteredResults;
    }

    @Override
    public Double getScore(String wineName, String region) {
        return wineRepository.getScore(wineName, region);
    }

    @Override
    public void incrementWineScore(String wineName, String region) {
        String redisValue = String.format("%s|%s", wineName, region);
        redisTemplate.opsForZSet().incrementScore("autocomplete:write", redisValue, 1);
    }
}
