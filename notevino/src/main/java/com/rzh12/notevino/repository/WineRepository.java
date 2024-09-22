package com.rzh12.notevino.repository;

import com.rzh12.notevino.dto.WineRequest;
import com.rzh12.notevino.dto.WineResponse;

import java.util.List;

public interface WineRepository {
    void saveWine(WineRequest wineRequest);

    List<WineResponse> findAllByUserId(Integer userId);

    boolean existsByIds(Integer wineId, Integer userId);

    void updateWine(Integer wineId, WineRequest wineRequest);

    void softDeleteWineById(Integer wineId);
}
