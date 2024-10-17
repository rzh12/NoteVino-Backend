package com.rzh12.notevino.repository.impl;

import com.rzh12.notevino.dto.FreeFormNoteRequest;
import com.rzh12.notevino.dto.FreeFormNoteResponse;
import com.rzh12.notevino.dto.NoteResponse;
import com.rzh12.notevino.dto.WineDetailsResponse;
import com.rzh12.notevino.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class NoteRepositoryImpl implements NoteRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public boolean existsWineByIdAndUserId(Integer wineId, Integer userId) {
        String sql = "SELECT COUNT(*) FROM user_uploaded_wines WHERE wine_id = ? AND user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{wineId, userId}, Integer.class);
        return count != null && count > 0;
    }

    @Override
    public FreeFormNoteResponse createFreeFormNoteAndReturnDetails(Integer wineId, Integer userId, FreeFormNoteRequest freeFormNoteRequest) {
        String noteSql = "INSERT INTO tasting_notes (wine_id, user_id, note_type) VALUES (?, ?, 'FreeForm')";
        jdbcTemplate.update(noteSql, wineId, userId);

        String getNoteIdSql = "SELECT LAST_INSERT_ID()";
        Integer noteId = jdbcTemplate.queryForObject(getNoteIdSql, Integer.class);

        String freeFormSql = "INSERT INTO freeform_notes (note_id, content) VALUES (?, ?)";
        jdbcTemplate.update(freeFormSql, noteId, freeFormNoteRequest.getContent());

        String getTimestampsSql = "SELECT created_at, updated_at FROM freeform_notes WHERE note_id = ?";
        return jdbcTemplate.queryForObject(getTimestampsSql, new Object[]{noteId}, (rs, rowNum) ->
                new FreeFormNoteResponse(
                        noteId,
                        freeFormNoteRequest.getContent(),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getTimestamp("updated_at").toLocalDateTime()
                )
        );
    }

    @Override
    public boolean existsWineById(Integer wineId) {
        String sql = "SELECT COUNT(*) FROM user_uploaded_wines WHERE wine_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{wineId}, Integer.class);
        return count != null && count > 0;
    }

    @Override
    public WineDetailsResponse getWineDetailsWithNotes(Integer wineId) {
        String wineSql = "SELECT wine_id, name, region, type, vintage, image_url, created_at FROM user_uploaded_wines WHERE wine_id = ?";
        WineDetailsResponse wineDetails = jdbcTemplate.queryForObject(wineSql, new Object[]{wineId}, (rs, rowNum) ->
                new WineDetailsResponse(
                        rs.getInt("wine_id"),
                        rs.getString("name"),
                        rs.getString("region"),
                        rs.getString("type"),
                        rs.getInt("vintage"),
                        rs.getString("image_url"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        null
                )
        );

        String noteTypeSql = "SELECT note_id, note_type FROM tasting_notes WHERE wine_id = ?";
        List<NoteResponse> allNotes = jdbcTemplate.query(noteTypeSql, new Object[]{wineId}, (rs, rowNum) -> {
            Integer noteId = rs.getInt("note_id");
            String noteType = rs.getString("note_type");

            return getNoteContent(noteId, noteType);
        });

        wineDetails.setNotes(allNotes);
        return wineDetails;
    }

    private NoteResponse getNoteContent(Integer noteId, String noteType) {
        switch (noteType) {
            case "FreeForm":
                String freeFormSql = "SELECT content, created_at, updated_at FROM freeform_notes WHERE note_id = ?";
                return jdbcTemplate.queryForObject(freeFormSql, new Object[]{noteId}, (rs, rowNum) ->
                        new FreeFormNoteResponse(
                                noteId,
                                rs.getString("content"),
                                rs.getTimestamp("created_at").toLocalDateTime(),
                                rs.getTimestamp("updated_at").toLocalDateTime()  // 包含 updated_at
                        )
                );
            default:
                throw new IllegalArgumentException("Unknown note type: " + noteType);
        }
    }

    @Override
    public boolean existsNoteByIdAndUserId(Integer noteId, Integer userId) {
        String sql = "SELECT COUNT(*) FROM tasting_notes WHERE note_id = ? AND user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{noteId, userId}, Integer.class);
        return count != null && count > 0;
    }

    @Override
    public LocalDateTime updateFreeFormNote(Integer noteId, FreeFormNoteRequest freeFormNoteRequest) {
        String sql = "UPDATE freeform_notes SET content = ? WHERE note_id = ?";
        jdbcTemplate.update(sql, freeFormNoteRequest.getContent(), noteId);

        String getUpdatedAtSql = "SELECT updated_at FROM freeform_notes WHERE note_id = ?";
        return jdbcTemplate.queryForObject(getUpdatedAtSql, new Object[]{noteId}, (rs, rowNum) ->
                rs.getTimestamp("updated_at").toLocalDateTime()
        );
    }

    @Override
    public boolean deleteNoteById(Integer noteId) {
        String sql = "DELETE FROM tasting_notes WHERE note_id = ?";
        jdbcTemplate.update(sql, noteId);
        return true;
    }
}
