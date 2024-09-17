package com.rzh12.notevino.repository;

import com.rzh12.notevino.dto.WineRequest;

public interface WineRepository {
    void saveWine(WineRequest wineRequest);
}
