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
        Integer userId = getCurrentUserId();

        if (satNoteRepository.existsByWineIdAndUserId(wineId, userId)) {
            return satNoteRepository.createSatNote(wineId, userId, satNoteRequest);
        }
        return null;
    }

    @Override
    public SatNoteResponse getSatNoteByWineId(Integer wineId) {

        Integer userId = getCurrentUserId();

        return satNoteRepository.findSatNoteByWineIdAndUserId(wineId, userId);
    }

    @Override
    public boolean updateSatNote(Integer wineId, SatNoteRequest satNoteRequest) {
        Integer userId = getCurrentUserId();

        if (satNoteRepository.existsByWineIdAndUserId(wineId, userId)) {
            return satNoteRepository.updateSatNote(wineId, userId, satNoteRequest);
        }

        return false;
    }

    private Integer getCurrentUserId() {
        UserDetailDTO currentUser = (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentUser.getUserId();
    }
}
