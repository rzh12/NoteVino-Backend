package com.rzh12.notevino.service.impl;

import com.rzh12.notevino.dto.SatNoteRequest;
import com.rzh12.notevino.dto.SatNoteResponse;
import com.rzh12.notevino.repository.SatNoteRepository;
import com.rzh12.notevino.service.SatNoteService;
import com.rzh12.notevino.service.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SatNoteServiceImpl implements SatNoteService {

    @Autowired
    private SatNoteRepository satNoteRepository;

    @Override
    public SatNoteResponse createSatNote(Integer wineId, SatNoteRequest satNoteRequest) {
        Integer userId = UserUtil.getCurrentUserId();

        if (satNoteRepository.existsByWineIdAndUserId(wineId, userId)) {
            return satNoteRepository.createSatNote(wineId, userId, satNoteRequest);
        }
        return null;
    }

    @Override
    public SatNoteResponse getSatNoteByWineId(Integer wineId) {

        Integer userId = UserUtil.getCurrentUserId();

        SatNoteResponse satNoteResponse = satNoteRepository.findSatNoteByWineIdAndUserId(wineId, userId);

        return satNoteResponse;
    }

    @Override
    public boolean updateSatNote(Integer wineId, SatNoteRequest satNoteRequest) {
        Integer userId = UserUtil.getCurrentUserId();

        if (satNoteRepository.existsByWineIdAndUserId(wineId, userId)) {
            return satNoteRepository.updateSatNote(wineId, userId, satNoteRequest);
        }

        return false;
    }
}
