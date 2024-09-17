package com.rzh12.notevino.service;

import com.rzh12.notevino.dto.WineRequest;
import org.springframework.web.multipart.MultipartFile;

public interface WineService {
    void addNewWine(WineRequest wineRequest, MultipartFile image);
}
