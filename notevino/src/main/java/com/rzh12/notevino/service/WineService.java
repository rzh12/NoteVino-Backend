package com.rzh12.notevino.service;

import com.rzh12.notevino.dto.WineRequest;
import com.rzh12.notevino.dto.WineResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface WineService {
    void addNewWine(WineRequest wineRequest, MultipartFile image);

    List<WineResponse> getUserUploadedWines();
}
