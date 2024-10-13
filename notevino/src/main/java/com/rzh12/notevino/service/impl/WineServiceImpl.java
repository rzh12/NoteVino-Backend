package com.rzh12.notevino.service.impl;

import com.rzh12.notevino.dto.UserDetailDTO;
import com.rzh12.notevino.dto.WineAutocompleteResponse;
import com.rzh12.notevino.dto.WineRequest;
import com.rzh12.notevino.dto.WineResponse;
import com.rzh12.notevino.repository.WineRepository;
import com.rzh12.notevino.service.S3Service;
import com.rzh12.notevino.service.WineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
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

        Integer userId = getCurrentUserId();
        wineRequest.setUserId(userId);

        return wineRepository.saveWine(wineRequest);
    }

    @Override
    public List<WineResponse> getUserUploadedWines() {
        Integer userId = getCurrentUserId();
        return wineRepository.findAllByUserId(userId);
    }

    @Override
    public boolean updateWine(Integer wineId, WineRequest wineRequest) {
        Integer userId = getCurrentUserId();

        if (wineRepository.existsByIdAndUserId(wineId, userId)) {
            wineRepository.updateWine(wineId, wineRequest);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteWine(Integer wineId) {
        Integer userId = getCurrentUserId();

        if (wineRepository.existsByIdAndUserId(wineId, userId)) {
            wineRepository.softDeleteWineById(wineId);
            return true;
        }
        return false;
    }

    @Override
    public List<WineResponse> searchWinesByName(String query) {
        Integer userId = getCurrentUserId();
        return wineRepository.searchWinesByNameAndUserId(query, userId);
    }

    @Override
    public List<WineAutocompleteResponse> autocompleteWines(String query) {
        // 如果查詢字串為空，直接顯示最熱門的酒款
        if (query == null || query.trim().isEmpty()) {
            // 從 Redis 中獲取熱門的前 10 個酒款 (ZSet)
            Set<Object> cachedResults = redisTemplate.opsForZSet().reverseRange("autocomplete:read", 0, 9);

            if (cachedResults != null && !cachedResults.isEmpty()) {
                // 將熱門結果轉換為 WineAutocompleteResponse，返回最多 10 個
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
                // 如果 Redis 中沒有數據，從資料庫查詢
                List<WineAutocompleteResponse> dbResults = wineRepository.getTopWines();

                // 更新 Redis
                for (WineAutocompleteResponse result : dbResults) {
                    String redisValue = String.format("%d|%s|%s", result.getWineId(), result.getName(), result.getRegion());
                    redisTemplate.opsForZSet().add("autocomplete:read", redisValue, result.getScore());
                }

                return dbResults;
            }
        }

        // 從 Redis 中獲取熱門的前 50 個酒款 (ZSet)
        Set<Object> cachedResults = redisTemplate.opsForZSet().reverseRange("autocomplete:read", 0, 49);

        // 查詢字串轉為小寫以便進行不區分大小寫的匹配
        String searchQuery = query.toLowerCase();

        // 準備結果列表
        List<WineAutocompleteResponse> filteredResults;

        // 在 Redis 中過濾符合查詢字串的結果
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
                    .limit(10)  // 優先返回 Redis 中最多 10 個匹配結果
                    .collect(Collectors.toList());
        } else {
            filteredResults = new ArrayList<>();
        }

        // 如果 Redis 中的結果不足 10 筆，從資料庫中補充
        if (filteredResults.size() < 10) {
            int remainingCount = 10 - filteredResults.size();
            List<WineAutocompleteResponse> dbResults = wineRepository.autocompleteWines(query);

            // 從資料庫結果中補充剩餘的部分
            List<WineAutocompleteResponse> additionalResults = dbResults.stream()
                    .filter(response -> filteredResults.stream().noneMatch(existing ->
                            existing.getWineId().equals(response.getWineId())))
                    .limit(remainingCount)
                    .collect(Collectors.toList());

            filteredResults.addAll(additionalResults);

            // 將從資料庫查詢的結果更新到 Redis 中
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

    private Integer getCurrentUserId() {
        UserDetailDTO currentUser = (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentUser.getUserId();
    }

}
