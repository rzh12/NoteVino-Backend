package com.rzh12.notevino.repository;

import com.rzh12.notevino.dto.FreeFormNoteRequest;
import com.rzh12.notevino.dto.WineDetailsResponse;

public interface NoteRepository {
    boolean existsWineByIdAndUserId(Integer wineId, Integer userId);

    boolean createFreeFormNote(Integer wineId, Integer userId, FreeFormNoteRequest freeFormNoteRequest);

    boolean existsWineById(Integer wineId);

    WineDetailsResponse getWineDetailsWithNotes(Integer wineId);

    boolean existsNoteByIdAndUserId(Integer noteId, Integer userId);

    boolean updateFreeFormNote(Integer noteId, FreeFormNoteRequest freeFormNoteRequest);

    boolean deleteNoteById(Integer noteId);
}
