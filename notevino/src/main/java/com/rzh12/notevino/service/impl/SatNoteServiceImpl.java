package com.rzh12.notevino.service.impl;

import com.rzh12.notevino.dto.SatNoteRequest;
import com.rzh12.notevino.dto.SatNoteResponse;
import com.rzh12.notevino.dto.UserDetailDTO;
import com.rzh12.notevino.repository.SatNoteRepository;
import com.rzh12.notevino.service.SatNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SatNoteServiceImpl implements SatNoteService {

    @Autowired
    private SatNoteRepository satNoteRepository;

    @Override
    public SatNoteResponse createSatNote(Integer wineId, SatNoteRequest satNoteRequest) {
        Integer userId = getCurrentUserId();  // 獲取當前使用者ID

        if (satNoteRepository.existsByWineIdAndUserId(wineId, userId)) {
            // 讓 Repository 負責創建並返回 SAT Note 的細節
            return satNoteRepository.createSatNote(wineId, userId, satNoteRequest);
        }
        return null;
    }

    @Override
    public SatNoteResponse getSatNoteByWineId(Integer wineId) {
        // 獲取當前用戶的 userId
        Integer userId = getCurrentUserId();

        // 查詢 SAT Note 並返回
        return satNoteRepository.findSatNoteByWineIdAndUserId(wineId, userId);
    }

    @Override
    public boolean updateSatNote(Integer wineId, SatNoteRequest satNoteRequest) {
        Integer userId = getCurrentUserId();  // 獲取當前使用者ID

        // 檢查該葡萄酒是否屬於當前使用者
        if (satNoteRepository.existsByWineIdAndUserId(wineId, userId)) {
            // 更新 SAT Note 並返回是否成功
            return satNoteRepository.updateSatNote(wineId, userId, satNoteRequest);
        }

        return false;  // 如果權限不足或記錄不存在，則返回 false
    }

    private Integer getCurrentUserId() {
        // 從 SecurityContext 中獲取當前用戶ID
        UserDetailDTO currentUser = (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentUser.getUserId();
    }
}
