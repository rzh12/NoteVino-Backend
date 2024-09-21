package com.rzh12.notevino.repository.impl;

import com.rzh12.notevino.dto.WineRequest;
import com.rzh12.notevino.dto.WineResponse;
import com.rzh12.notevino.repository.WineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WineRepositoryImpl implements WineRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void saveWine(WineRequest wineRequest) {
        String sql = "INSERT INTO user_uploaded_wines (user_id, name, region, type, vintage, image_url) VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                wineRequest.getUserId(),
                wineRequest.getName(),
                wineRequest.getRegion(),
                wineRequest.getType(),
                wineRequest.getVintage(),
                wineRequest.getImageUrl()
        );
    }

    @Override
    public List<WineResponse> findAllByUserId(Integer userId) {
        String sql = "SELECT wine_id, name, region, type, vintage, image_url FROM user_uploaded_wines WHERE user_id = ?";
        return jdbcTemplate.query(sql, new Object[]{userId}, (rs, rowNum) ->
                new WineResponse(
                        rs.getInt("wine_id"),
                        rs.getString("name"),
                        rs.getString("region"),
                        rs.getString("type"),
                        rs.getInt("vintage"),
                        rs.getString("image_url")
                )
        );
    }
}
