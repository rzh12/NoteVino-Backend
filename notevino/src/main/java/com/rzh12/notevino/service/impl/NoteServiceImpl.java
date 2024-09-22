package com.rzh12.notevino.service.impl;

import com.rzh12.notevino.dto.FreeFormNoteRequest;
import com.rzh12.notevino.dto.WineDetailsResponse;
import com.rzh12.notevino.repository.NoteRepository;
import com.rzh12.notevino.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NoteServiceImpl implements NoteService {

    @Autowired
    private NoteRepository noteRepository;

    @Override
    public boolean createFreeFormNote(Integer wineId, FreeFormNoteRequest freeFormNoteRequest) {
        Integer userId = getCurrentUserId();  // 獲取當前使用者ID

        // 檢查該葡萄酒是否屬於當前使用者
        if (noteRepository.existsWineByIdAndUserId(wineId, userId)) {
            // 創建 Tasting Note 和 FreeForm Note
            return noteRepository.createFreeFormNote(wineId, userId, freeFormNoteRequest);
        }
        return false;
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
    public boolean updateFreeFormNote(Integer wineId, Integer noteId, FreeFormNoteRequest freeFormNoteRequest) {
        Integer userId = getCurrentUserId();  // 獲取當前使用者ID

        // 檢查該葡萄酒是否屬於當前使用者，並且筆記也屬於該使用者
        if (noteRepository.existsWineByIdAndUserId(wineId, userId) && noteRepository.existsNoteByIdAndUserId(noteId, userId)) {
            // 更新 FreeForm 筆記
            return noteRepository.updateFreeFormNote(noteId, freeFormNoteRequest);
        }
        return false;
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
        // 從安全上下文中獲取當前使用者ID
        return 1;  // 假設目前為用戶 ID 1
    }
}
