package com.rzh12.notevino.service;

import com.rzh12.notevino.dto.WineAutocompleteResponse;
import com.rzh12.notevino.dto.WineRequest;
import com.rzh12.notevino.dto.WineResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface WineService {

    Integer addNewWine(WineRequest wineRequest, MultipartFile image);

    List<WineResponse> getUserUploadedWines();

    boolean updateWine(Integer wineId, WineRequest wineRequest);

    boolean deleteWine(Integer wineId);

    List<WineResponse> searchWinesByName(String query);

    List<WineAutocompleteResponse> autocompleteWines(String query);

    Double getScore(String wineName, String region);

    void incrementWineScore(String wineName, String region);
}
