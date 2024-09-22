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

    @Override
    public boolean updateWine(Integer wineId, WineRequest wineRequest) {
        Integer userId = getCurrentUserId();  // 獲取當前使用者ID

        // 檢查是否存在該葡萄酒、檢查該葡萄酒是否屬於當前使用者
        if (wineRepository.existsByIdAndUserId(wineId, userId)) {
            // 更新葡萄酒信息，不能更新 imageUrl
            wineRepository.updateWine(wineId, wineRequest);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteWine(Integer wineId) {
        Integer userId = getCurrentUserId();  // 獲取當前使用者ID

        // 檢查該葡萄酒是否屬於當前使用者
        if (wineRepository.existsByIdAndUserId(wineId, userId)) {
            // 刪除葡萄酒和相關的品鑒筆記
            wineRepository.softDeleteWineById(wineId);
            return true;
        }
        return false;
    }

    private Integer getCurrentUserId() {
        // 從安全上下文中獲取當前使用者ID
        // 假設我們有 JWT 或 Spring Security 可以幫助取得 userId
        return 1;  // 假設目前為用戶 ID 1
    }

}
