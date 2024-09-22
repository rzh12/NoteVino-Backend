package com.rzh12.notevino.service;

import com.rzh12.notevino.dto.FreeFormNoteRequest;
import com.rzh12.notevino.dto.WineDetailsResponse;

public interface NoteService {
    boolean createFreeFormNote(Integer wineId, FreeFormNoteRequest freeFormNoteRequest);

    WineDetailsResponse getWineDetailsWithNotes(Integer wineId);

    boolean updateFreeFormNote(Integer wineId, Integer noteId, FreeFormNoteRequest freeFormNoteRequest);

    boolean deleteNote(Integer wineId, Integer noteId);
}
