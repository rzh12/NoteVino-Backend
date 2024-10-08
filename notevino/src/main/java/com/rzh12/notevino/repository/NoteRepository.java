package com.rzh12.notevino.repository;

import com.rzh12.notevino.dto.FreeFormNoteRequest;
import com.rzh12.notevino.dto.FreeFormNoteResponse;
import com.rzh12.notevino.dto.WineDetailsResponse;

import java.time.LocalDateTime;

public interface NoteRepository {
    boolean existsWineByIdAndUserId(Integer wineId, Integer userId);

    FreeFormNoteResponse createFreeFormNoteAndReturnDetails(Integer wineId, Integer userId, FreeFormNoteRequest freeFormNoteRequest);

    boolean existsWineById(Integer wineId);

    WineDetailsResponse getWineDetailsWithNotes(Integer wineId);

    boolean existsNoteByIdAndUserId(Integer noteId, Integer userId);

    LocalDateTime updateFreeFormNote(Integer noteId, FreeFormNoteRequest freeFormNoteRequest);

    boolean deleteNoteById(Integer noteId);
}
