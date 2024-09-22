package com.rzh12.notevino.repository.impl;

import com.rzh12.notevino.dto.FreeFormNoteRequest;
import com.rzh12.notevino.dto.FreeFormNoteResponse;
import com.rzh12.notevino.dto.NoteResponse;
import com.rzh12.notevino.dto.WineDetailsResponse;
import com.rzh12.notevino.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NoteRepositoryImpl implements NoteRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public boolean existsWineByIds(Integer wineId, Integer userId) {
        String sql = "SELECT COUNT(*) FROM user_uploaded_wines WHERE wine_id = ? AND user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{wineId, userId}, Integer.class);
        return count != null && count > 0;
    }

    @Override
    public boolean createFreeFormNote(Integer wineId, Integer userId, FreeFormNoteRequest freeFormNoteRequest) {
        // 插入 tasting_notes
        String noteSql = "INSERT INTO tasting_notes (wine_id, user_id, note_type) VALUES (?, ?, 'FreeForm')";
        jdbcTemplate.update(noteSql, wineId, userId);

        // 取得剛剛插入的 note_id
        String getNoteIdSql = "SELECT LAST_INSERT_ID()";
        Integer noteId = jdbcTemplate.queryForObject(getNoteIdSql, Integer.class);

        // 插入 freeform_notes
        String freeFormSql = "INSERT INTO freeform_notes (note_id, content) VALUES (?, ?)";
        jdbcTemplate.update(freeFormSql, noteId, freeFormNoteRequest.getContent());

        return true;
    }

    @Override
    public boolean existsWineById(Integer wineId) {
        String sql = "SELECT COUNT(*) FROM user_uploaded_wines WHERE wine_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{wineId}, Integer.class);
        return count != null && count > 0;
    }

    @Override
    public WineDetailsResponse getWineDetailsWithNotes(Integer wineId) {
        // 取得葡萄酒詳細資訊
        String wineSql = "SELECT wine_id, name, region, type, vintage, image_url FROM user_uploaded_wines WHERE wine_id = ?";
        WineDetailsResponse wineDetails = jdbcTemplate.queryForObject(wineSql, new Object[]{wineId}, (rs, rowNum) ->
                new WineDetailsResponse(
                        rs.getInt("wine_id"),
                        rs.getString("name"),
                        rs.getString("region"),
                        rs.getString("type"),
                        rs.getInt("vintage"),
                        rs.getString("image_url"),
                        null  // 筆記將會在後面取得
                )
        );

        // 查詢 tasting_notes 表取得所有類型的筆記
        String noteTypeSql = "SELECT note_id, note_type FROM tasting_notes WHERE wine_id = ?";
        List<NoteResponse> allNotes = jdbcTemplate.query(noteTypeSql, new Object[]{wineId}, (rs, rowNum) -> {
            Integer noteId = rs.getInt("note_id");
            String noteType = rs.getString("note_type");

            // 根據 note_type 查詢對應的筆記內容
            return getNoteContent(noteId, noteType);
        });

        // 設置筆記
        wineDetails.setNotes(allNotes);
        return wineDetails;
    }

    // 動態查詢不同格式的筆記內容
    private NoteResponse getNoteContent(Integer noteId, String noteType) {
        switch (noteType) {
            case "FreeForm":
                String freeFormSql = "SELECT content, created_at FROM freeform_notes WHERE note_id = ?";
                return jdbcTemplate.queryForObject(freeFormSql, new Object[]{noteId}, (rs, rowNum) ->
                        new FreeFormNoteResponse(rs.getString("content"), rs.getTimestamp("created_at").toLocalDateTime())
                );
            default:
                throw new IllegalArgumentException("Unknown note type: " + noteType);
        }
    }
}
