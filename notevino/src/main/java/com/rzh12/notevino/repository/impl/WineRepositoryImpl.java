package com.rzh12.notevino.repository.impl;

import com.rzh12.notevino.dto.WineRequest;
import com.rzh12.notevino.repository.WineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class WineRepositoryImpl implements WineRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void saveWine(WineRequest wineRequest) {
        String sql = "INSERT INTO user_uploaded_wines (name, region, type, vintage, image_url) VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                wineRequest.getName(),
                wineRequest.getRegion(),
                wineRequest.getType(),
                wineRequest.getVintage(),
                wineRequest.getImageUrl()
        );
    }
}
