package com.rzh12.notevino.service.impl;

import com.rzh12.notevino.dto.WineRequest;
import com.rzh12.notevino.dto.WineResponse;
import com.rzh12.notevino.repository.WineRepository;
import com.rzh12.notevino.service.S3Service;
import com.rzh12.notevino.service.WineService;
import org.springframework.beans.factory.annotation.Autowired;
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
        // 上傳圖片到 S3 並獲取 URL
        if (image != null && !image.isEmpty()) {
            String imageUrl = s3Service.uploadFile(image);
            wineRequest.setImageUrl(imageUrl);
        }

        // 將當前用戶ID設置到 wineRequest 中
        Integer userId = getCurrentUserId(); // 假設你有一個取得當前用戶ID的方法
        wineRequest.setUserId(userId);

        // 將資料儲存到資料庫
        wineRepository.saveWine(wineRequest);
    }

    @Override
    public List<WineResponse> getUserUploadedWines() {
        Integer userId = getCurrentUserId(); // 假設你有一個取得用戶ID的方法
        return wineRepository.findAllByUserId(userId);
    }

    private Integer getCurrentUserId() {
        // 取得當前用戶ID的邏輯，這裡可以集成你的用戶認證系統
        return 1;  // 示例用戶ID
    }
}
