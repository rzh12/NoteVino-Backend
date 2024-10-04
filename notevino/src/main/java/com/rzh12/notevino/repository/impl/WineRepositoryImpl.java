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
        String sql = "SELECT wine_id, name, region, type, vintage, image_url FROM user_uploaded_wines WHERE user_id = ? AND is_deleted = 0";
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

    @Override
    public boolean existsByIdAndUserId(Integer wineId, Integer userId) {
        String sql = "SELECT COUNT(*) FROM user_uploaded_wines WHERE wine_id = ? AND user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{wineId, userId}, Integer.class);
        return count != null && count > 0;
    }

    @Override
    public void updateWine(Integer wineId, WineRequest wineRequest) {
        String sql = "UPDATE user_uploaded_wines SET name = ?, region = ?, type = ?, vintage = ? WHERE wine_id = ?";
        jdbcTemplate.update(sql,
                wineRequest.getName(),
                wineRequest.getRegion(),
                wineRequest.getType(),
                wineRequest.getVintage(),
                wineId
        );
    }

    @Override
    public void softDeleteWineById(Integer wineId) {
        String sql = "UPDATE user_uploaded_wines SET is_deleted = 1 WHERE wine_id = ?";
        jdbcTemplate.update(sql, wineId);
    }

    @Override
    public List<WineResponse> searchWinesByNameAndUserId(String query, Integer userId) {
        String sql = "SELECT wine_id, name, region, type, vintage, image_url FROM user_uploaded_wines WHERE name LIKE ? AND user_id = ? AND is_deleted = 0";
        String searchQuery = "%" + query + "%";
        return jdbcTemplate.query(sql, new Object[]{searchQuery, userId}, (rs, rowNum) ->
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
