package com.rzh12.notevino.service.impl;

import com.rzh12.notevino.dto.FreeFormNoteRequest;
import com.rzh12.notevino.dto.FreeFormNoteResponse;
import com.rzh12.notevino.dto.WineDetailsResponse;
import com.rzh12.notevino.repository.NoteRepository;
import com.rzh12.notevino.service.NoteService;
import com.rzh12.notevino.service.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NoteServiceImpl implements NoteService {

    @Autowired
    private NoteRepository noteRepository;

    @Override
    public FreeFormNoteResponse createFreeFormNote(Integer wineId, FreeFormNoteRequest freeFormNoteRequest) {
        Integer userId = UserUtil.getCurrentUserId();

        if (noteRepository.existsWineByIdAndUserId(wineId, userId)) {
            return noteRepository.createFreeFormNoteAndReturnDetails(wineId, userId, freeFormNoteRequest);
        }
        return null;
    }

    @Override
    public WineDetailsResponse getWineDetailsWithNotes(Integer wineId) {

        if (noteRepository.existsWineById(wineId)) {
            return noteRepository.getWineDetailsWithNotes(wineId);
        }
        return null;
    }

    @Override
    public LocalDateTime updateFreeFormNote(Integer wineId, Integer noteId, FreeFormNoteRequest freeFormNoteRequest) {
        Integer userId = UserUtil.getCurrentUserId();

        if (noteRepository.existsWineByIdAndUserId(wineId, userId) && noteRepository.existsNoteByIdAndUserId(noteId, userId)) {
            return noteRepository.updateFreeFormNote(noteId, freeFormNoteRequest);
        }
        throw new IllegalArgumentException("Wine or Note does not belong to the current user.");
    }

    @Override
    public boolean deleteNote(Integer wineId, Integer noteId) {
        Integer userId = UserUtil.getCurrentUserId();

        if (noteRepository.existsWineByIdAndUserId(wineId, userId) && noteRepository.existsNoteByIdAndUserId(noteId, userId)) {
            return noteRepository.deleteNoteById(noteId);
        }
        return false;
    }

}
