package com.rzh12.notevino.service.impl;

import com.rzh12.notevino.dto.UserDetailDTO;
import com.rzh12.notevino.dto.WineRequest;
import com.rzh12.notevino.dto.WineResponse;
import com.rzh12.notevino.repository.WineRepository;
import com.rzh12.notevino.service.S3Service;
import com.rzh12.notevino.service.WineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class WineServiceImpl implements WineService {

    @Autowired
    private WineRepository wineRepository;

    @Autowired
    private S3Service s3Service;

    @Override
    public void addNewWine(WineRequest wineRequest, MultipartFile image) {
        if (image != null && !image.isEmpty()) {
            String imageUrl = s3Service.uploadFile(image, "wine");
            wineRequest.setImageUrl(imageUrl);
        }

        Integer userId = getCurrentUserId();
        wineRequest.setUserId(userId);

        wineRepository.saveWine(wineRequest);
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

    private Integer getCurrentUserId() {
        UserDetailDTO currentUser = (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentUser.getUserId();
    }

}
