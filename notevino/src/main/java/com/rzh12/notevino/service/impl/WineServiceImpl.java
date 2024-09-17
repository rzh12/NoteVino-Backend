package com.rzh12.notevino.service.impl;

import com.rzh12.notevino.dto.WineRequest;
import com.rzh12.notevino.repository.WineRepository;
import com.rzh12.notevino.service.S3Service;
import com.rzh12.notevino.service.WineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

        // 將資料儲存到資料庫
        wineRepository.saveWine(wineRequest);
    }
}
