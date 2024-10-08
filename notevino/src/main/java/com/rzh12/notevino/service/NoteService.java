package com.rzh12.notevino.service;

import com.rzh12.notevino.dto.FreeFormNoteRequest;
import com.rzh12.notevino.dto.FreeFormNoteResponse;
import com.rzh12.notevino.dto.WineDetailsResponse;

import java.time.LocalDateTime;

public interface NoteService {
    FreeFormNoteResponse createFreeFormNote(Integer wineId, FreeFormNoteRequest freeFormNoteRequest);

    WineDetailsResponse getWineDetailsWithNotes(Integer wineId);

    LocalDateTime updateFreeFormNote(Integer wineId, Integer noteId, FreeFormNoteRequest freeFormNoteRequest);

    boolean deleteNote(Integer wineId, Integer noteId);
}
