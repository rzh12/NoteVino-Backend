package com.rzh12.notevino.repository;

import com.rzh12.notevino.dto.FreeFormNoteRequest;
import com.rzh12.notevino.dto.WineDetailsResponse;

public interface NoteRepository {
    boolean existsWineByIds(Integer wineId, Integer userId);

    boolean createFreeFormNote(Integer wineId, Integer userId, FreeFormNoteRequest freeFormNoteRequest);

    boolean existsWineById(Integer wineId);

    WineDetailsResponse getWineDetailsWithNotes(Integer wineId);
}
