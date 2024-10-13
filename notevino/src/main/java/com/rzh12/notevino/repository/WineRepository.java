package com.rzh12.notevino.repository;

import com.rzh12.notevino.dto.WineAutocompleteResponse;
import com.rzh12.notevino.dto.WineRequest;
import com.rzh12.notevino.dto.WineResponse;

import java.util.List;

public interface WineRepository {

    Integer saveWine(WineRequest wineRequest);

    List<WineResponse> findAllByUserId(Integer userId);

    boolean existsByIdAndUserId(Integer wineId, Integer userId);

    void updateWine(Integer wineId, WineRequest wineRequest);

    void softDeleteWineById(Integer wineId);

    List<WineResponse> searchWinesByNameAndUserId(String query, Integer userId);

    void updateWineScore(String wineName, String region, Double currentScore);

    List<WineAutocompleteResponse> autocompleteWines(String query);

    List<WineAutocompleteResponse> getTopWines();

    Double getScore(String wineName, String region);
}
