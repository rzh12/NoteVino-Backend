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
     * Periodically batch write the accumulated scores from Redis into the database.
     * Use the `autocomplete:write` key to accumulate write operations, then periodically update the database.
     */
    @Scheduled(fixedRate = 60000)
    public void persistScoresToDB() {
        Set<ZSetOperations.TypedTuple<Object>> scores = redisTemplate.opsForZSet().rangeWithScores("autocomplete:write", 0, -1);

        if (scores != null && !scores.isEmpty()) {
            for (ZSetOperations.TypedTuple<Object> score : scores) {
                String redisValue = score.getValue().toString();
                Double currentScore = score.getScore();

                String[] parts = redisValue.split("\\|");
                String wineName = parts[0];
                String region = parts[1];

                wineRepository.updateWineScore(wineName, region, currentScore);
            }

            redisTemplate.delete("autocomplete:write");
        }
    }

    /**
     * Periodically fetch the latest scores from the database and refresh the autocomplete cache in Redis.
     * Use the `autocomplete:read` key to store data retrieved from the database for autocomplete purposes.
     */
    @Scheduled(fixedRate = 60000)
    public void refreshAutocompleteCache() {
        List<WineAutocompleteResponse> wineList = wineRepository.getTopWines();

        redisTemplate.delete("autocomplete:read");

        for (WineAutocompleteResponse wine : wineList) {
            String redisValue = String.format("%d|%s|%s", wine.getWineId(), wine.getName(), wine.getRegion());
            redisTemplate.opsForZSet().add("autocomplete:read", redisValue, wine.getScore());
        }

        redisTemplate.expire("autocomplete:read", 1, TimeUnit.HOURS);
    }
}
