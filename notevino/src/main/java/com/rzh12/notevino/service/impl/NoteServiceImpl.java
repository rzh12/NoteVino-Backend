package com.rzh12.notevino.service.impl;

import com.rzh12.notevino.dto.FreeFormNoteRequest;
import com.rzh12.notevino.dto.FreeFormNoteResponse;
import com.rzh12.notevino.dto.UserDetailDTO;
import com.rzh12.notevino.dto.WineDetailsResponse;
import com.rzh12.notevino.repository.NoteRepository;
import com.rzh12.notevino.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NoteServiceImpl implements NoteService {

    @Autowired
    private NoteRepository noteRepository;

    @Override
    public FreeFormNoteResponse createFreeFormNote(Integer wineId, FreeFormNoteRequest freeFormNoteRequest) {
        Integer userId = getCurrentUserId();  // 獲取當前使用者ID

        if (noteRepository.existsWineByIdAndUserId(wineId, userId)) {
            // 創建並返回新的筆記，包括 noteId 和 created_at
            return noteRepository.createFreeFormNoteAndReturnDetails(wineId, userId, freeFormNoteRequest);
        }
        return null;
    }

    @Override
    public WineDetailsResponse getWineDetailsWithNotes(Integer wineId) {
        // 檢查葡萄酒是否存在
        if (noteRepository.existsWineById(wineId)) {
            // 取得葡萄酒詳細資訊和相關的 Tasting Notes
            return noteRepository.getWineDetailsWithNotes(wineId);
        }
        return null;
    }

    @Override
    public LocalDateTime updateFreeFormNote(Integer wineId, Integer noteId, FreeFormNoteRequest freeFormNoteRequest) {
        Integer userId = getCurrentUserId();  // 獲取當前使用者ID

        // 檢查該葡萄酒是否屬於當前使用者，並且筆記也屬於該使用者
        if (noteRepository.existsWineByIdAndUserId(wineId, userId) && noteRepository.existsNoteByIdAndUserId(noteId, userId)) {
            // 更新 FreeForm 筆記，並返回更新的時間
            return noteRepository.updateFreeFormNote(noteId, freeFormNoteRequest);
        }
        throw new IllegalArgumentException("Wine or Note does not belong to the current user.");
    }

    @Override
    public boolean deleteNote(Integer wineId, Integer noteId) {
        Integer userId = getCurrentUserId();  // 獲取當前使用者ID

        // 檢查該葡萄酒和筆記是否屬於當前使用者
        if (noteRepository.existsWineByIdAndUserId(wineId, userId) && noteRepository.existsNoteByIdAndUserId(noteId, userId)) {
            // 刪除筆記
            return noteRepository.deleteNoteById(noteId);
        }
        return false;
    }

    private Integer getCurrentUserId() {
        // 從 SecurityContext 中獲取當前用戶ID
        UserDetailDTO currentUser = (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentUser.getUserId();
    }
}
