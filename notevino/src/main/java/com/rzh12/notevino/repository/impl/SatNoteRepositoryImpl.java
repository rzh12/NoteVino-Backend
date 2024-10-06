package com.rzh12.notevino.repository.impl;

import com.rzh12.notevino.dto.SatNoteRequest;
import com.rzh12.notevino.dto.SatNoteResponse;
import com.rzh12.notevino.repository.SatNoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SatNoteRepositoryImpl implements SatNoteRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public boolean existsByWineIdAndUserId(Integer wineId, Integer userId) {
        String sql = "SELECT COUNT(*) FROM user_uploaded_wines WHERE wine_id = ? AND user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{wineId, userId}, Integer.class);
        return count != null && count > 0;
    }

    @Override
    public SatNoteResponse createSatNote(Integer wineId, Integer userId, SatNoteRequest satNoteRequest) {
        // 檢查該酒是否屬於當前使用者
        if (!existsByWineIdAndUserId(wineId, userId)) {
            throw new IllegalArgumentException("User does not have permission to create a SAT note for this wine.");
        }

        // 插入 sat_notes
        String satNoteSql = "INSERT INTO sat_notes (wine_id, user_id, sweetness, acidity, tannin, alcohol, body, flavour_intensity, finish, quality, potential_for_ageing) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(satNoteSql, wineId, userId, satNoteRequest.getSweetness(), satNoteRequest.getAcidity(),
                satNoteRequest.getTannin(), satNoteRequest.getAlcohol(), satNoteRequest.getBody(),
                satNoteRequest.getFlavourIntensity(), satNoteRequest.getFinish(),
                satNoteRequest.getQuality(), satNoteRequest.getPotentialForAgeing());

        // 查詢剛剛插入的 SAT Note 內容
        String getSatNoteSql = "SELECT sweetness, acidity, tannin, alcohol, body, flavour_intensity, finish, quality, potential_for_ageing FROM sat_notes WHERE wine_id = ? AND user_id = ?";
        return jdbcTemplate.queryForObject(getSatNoteSql, new Object[]{wineId, userId}, (rs, rowNum) ->
                new SatNoteResponse(
                        rs.getString("sweetness"),
                        rs.getString("acidity"),
                        rs.getString("tannin"),
                        rs.getString("alcohol"),
                        rs.getString("body"),
                        rs.getString("flavour_intensity"),
                        rs.getString("finish"),
                        rs.getString("quality"),
                        rs.getString("potential_for_ageing")
                )
        );
    }


    @Override
    public boolean updateSatNote(Integer wineId, Integer userId, SatNoteRequest satNoteRequest) {
        if (!existsByWineIdAndUserId(wineId, userId)) {
            throw new IllegalArgumentException("User does not have permission to update this SAT note.");
        }

        String sql = "UPDATE sat_notes SET sweetness = ?, acidity = ?, tannin = ?, alcohol = ?, body = ?, flavour_intensity = ?, finish = ?, " +
                "quality = ?, potential_for_ageing = ? WHERE wine_id = ? AND user_id = ?";

        jdbcTemplate.update(sql, satNoteRequest.getSweetness(), satNoteRequest.getAcidity(), satNoteRequest.getTannin(),
                satNoteRequest.getAlcohol(), satNoteRequest.getBody(), satNoteRequest.getFlavourIntensity(),
                satNoteRequest.getFinish(), satNoteRequest.getQuality(), satNoteRequest.getPotentialForAgeing(),
                wineId, userId);

        return true;
    }

    @Override
    public SatNoteResponse findSatNoteByWineIdAndUserId(Integer wineId, Integer userId) {
        if (!existsByWineIdAndUserId(wineId, userId)) {
            throw new IllegalArgumentException("User does not have permission to view this SAT note.");
        }
        String sql = "SELECT sweetness, acidity, tannin, alcohol, body, flavour_intensity, finish, quality, potential_for_ageing FROM sat_notes WHERE wine_id = ? AND user_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{wineId, userId}, (rs, rowNum) ->
                new SatNoteResponse(
                        rs.getString("sweetness"),
                        rs.getString("acidity"),
                        rs.getString("tannin"),
                        rs.getString("alcohol"),
                        rs.getString("body"),
                        rs.getString("flavour_intensity"),
                        rs.getString("finish"),
                        rs.getString("quality"),
                        rs.getString("potential_for_ageing")
                )
        );
    }

//    @Override
//    public boolean deleteSatNoteByWineIdAndUserId(Integer wineId, Integer userId) {
//        if (!existsByWineIdAndUserId(wineId, userId)) {
//            throw new IllegalArgumentException("User does not have permission to delete this SAT note.");
//        }
//        String sql = "DELETE FROM sat_notes WHERE wine_id = ? AND user_id = ?";
//        jdbcTemplate.update(sql, wineId, userId);
//        return true;
//    }
}
