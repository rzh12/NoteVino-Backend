package com.rzh12.notevino.service;

import com.rzh12.notevino.dto.SatNoteRequest;
import com.rzh12.notevino.dto.SatNoteResponse;

public interface SatNoteService {


    SatNoteResponse createSatNote(Integer wineId, SatNoteRequest satNoteRequest);

    SatNoteResponse getSatNoteByWineId(Integer wineId);

    boolean updateSatNote(Integer wineId, SatNoteRequest satNoteRequest);
}
