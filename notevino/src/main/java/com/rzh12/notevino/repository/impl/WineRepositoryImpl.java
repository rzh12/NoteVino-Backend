package com.rzh12.notevino.repository.impl;

import com.rzh12.notevino.dto.WineAutocompleteResponse;
import com.rzh12.notevino.dto.WineRequest;
import com.rzh12.notevino.dto.WineResponse;
import com.rzh12.notevino.repository.WineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class WineRepositoryImpl implements WineRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Integer saveWine(WineRequest wineRequest) {
        String sql = "INSERT INTO user_uploaded_wines (user_id, name, region, type, vintage, image_url) VALUES (?, ?, ?, ?, ?, ?)";

        // 使用 KeyHolder 來捕獲自增 ID
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, wineRequest.getUserId());
            ps.setString(2, wineRequest.getName());
            ps.setString(3, wineRequest.getRegion());
            ps.setString(4, wineRequest.getType());
            ps.setInt(5, wineRequest.getVintage());
            ps.setString(6, wineRequest.getImageUrl());
            return ps;
        }, keyHolder);

        // 從 keyHolder 中取得自增 ID 並返回
        return keyHolder.getKey().intValue();
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

    @Override
    public void updateWineScore(String wineName, String region, Double currentScore) {
        String sql = "UPDATE user_uploaded_wines SET score = score + ? WHERE name = ? AND region = ?";

        jdbcTemplate.update(sql, currentScore, wineName, region);
    }

    @Override
    public List<WineAutocompleteResponse> autocompleteWines(String query) {
        String sql = "SELECT uw.wine_id, uw.name, uw.region, uw.score " +
                "FROM user_uploaded_wines uw " +
                "JOIN ( " +
                "    SELECT name, region, MAX(score) AS max_score, MIN(wine_id) AS min_wine_id " +
                "    FROM user_uploaded_wines " +
                "    WHERE (name LIKE ? OR region LIKE ?) AND is_deleted = 0 " +
                "    GROUP BY name, region " +
                ") max_wines ON uw.name = max_wines.name " +
                "AND uw.region = max_wines.region " +
                "AND uw.score = max_wines.max_score " +
                "AND uw.wine_id = max_wines.min_wine_id " +
                "ORDER BY uw.score DESC, uw.wine_id ASC LIMIT 10";

        String searchQuery = "%" + query + "%";

        return jdbcTemplate.query(sql, new Object[]{searchQuery, searchQuery}, (rs, rowNum) ->
                new WineAutocompleteResponse(
                        rs.getInt("wine_id"),
                        rs.getString("name"),
                        rs.getString("region"),
                        rs.getDouble("score")
                )
        );
    }


    @Override
    public List<WineAutocompleteResponse> getTopWines() {
        String sql = "SELECT uw.wine_id, uw.name, uw.region, uw.score " +
                "FROM user_uploaded_wines uw " +
                "JOIN ( " +
                "    SELECT name, region, MAX(score) AS max_score, MIN(wine_id) AS min_wine_id " +
                "    FROM user_uploaded_wines " +
                "    WHERE is_deleted = 0 " +
                "    GROUP BY name, region " +
                ") max_wines ON uw.name = max_wines.name " +
                "AND uw.region = max_wines.region " +
                "AND uw.score = max_wines.max_score " +
                "AND uw.wine_id = max_wines.min_wine_id " +
                "ORDER BY uw.score DESC, uw.wine_id ASC LIMIT 10";

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new WineAutocompleteResponse(
                        rs.getInt("wine_id"),
                        rs.getString("name"),
                        rs.getString("region"),
                        rs.getDouble("score")
                )
        );
    }


    @Override
    public Double getScore(String wineName, String region) {
        String sql = "SELECT MAX(score) FROM user_uploaded_wines WHERE name = ? AND region = ? ORDER BY score DESC, wine_id ASC LIMIT 1";
        return jdbcTemplate.queryForObject(sql, new Object[]{wineName, region}, Double.class);
    }
}
