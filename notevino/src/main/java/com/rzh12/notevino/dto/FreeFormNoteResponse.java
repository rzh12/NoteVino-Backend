package com.rzh12.notevino.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FreeFormNoteResponse implements NoteResponse {
    private Integer noteId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
