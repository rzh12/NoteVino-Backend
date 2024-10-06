package com.rzh12.notevino.repository;

import com.rzh12.notevino.dto.SatNoteRequest;
import com.rzh12.notevino.dto.SatNoteResponse;

public interface SatNoteRepository {
    boolean existsByWineIdAndUserId(Integer wineId, Integer userId);

    SatNoteResponse createSatNote(Integer wineId, Integer userId, SatNoteRequest satNoteRequest);

    boolean updateSatNote(Integer wineId, Integer userId, SatNoteRequest satNoteRequest);

    SatNoteResponse findSatNoteByWineIdAndUserId(Integer wineId, Integer userId);
}
