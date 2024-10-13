package com.rzh12.notevino.service;

import com.rzh12.notevino.dto.WineAutocompleteResponse;
import com.rzh12.notevino.repository.WineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class AutoCompleteCacheService {

    @Autowired
    private WineRepository wineRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 定期將 Redis 中的累積分數批次寫入資料庫。
     * 使用 `autocomplete:write` 鍵來累積寫操作，然後定期更新到資料庫。
     */
    @Scheduled(fixedRate = 60000)  // 每 60 秒執行一次
    public void persistScoresToDB() {
        Set<ZSetOperations.TypedTuple<Object>> scores = redisTemplate.opsForZSet().rangeWithScores("autocomplete:write", 0, -1);

        if (scores != null && !scores.isEmpty()) {
            for (ZSetOperations.TypedTuple<Object> score : scores) {
                String redisValue = score.getValue().toString();
                Double currentScore = score.getScore();

                // 拆解 redisValue 成 name 和 region
                String[] parts = redisValue.split("\\|");
                String wineName = parts[0];
                String region = parts[1];

                // 將分數寫入資料庫，針對所有符合 name 和 region 的酒款
                wineRepository.updateWineScore(wineName, region, currentScore);
            }

            // 清除 Redis 中累積的 write key
            redisTemplate.delete("autocomplete:write");
        }
    }

    /**
     * 定期從資料庫獲取最新的分數，並刷新 Redis 中的 autocomplete 快取。
     * 使用 `autocomplete:read` 鍵來存儲從資料庫中獲取的數據，用於自動完成。
     */
    @Scheduled(fixedRate = 60000)  // 每 60 秒執行一次
    public void refreshAutocompleteCache() {
        // 從資料庫獲取最新的分數列表
        List<WineAutocompleteResponse> wineList = wineRepository.getTopWines();

        // 清除 Redis 中舊的 autocomplete:read 快取
        redisTemplate.delete("autocomplete:read");

        // 將最新的分數寫入 Redis，使用 wineId|wineName|region 格式
        for (WineAutocompleteResponse wine : wineList) {
            String redisValue = String.format("%d|%s|%s", wine.getWineId(), wine.getName(), wine.getRegion());
            redisTemplate.opsForZSet().add("autocomplete:read", redisValue, wine.getScore());
        }

        // 設置 autocomplete:read 的 TTL 為 1 小時
        redisTemplate.expire("autocomplete:read", 1, TimeUnit.HOURS);
    }
}
