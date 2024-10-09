package com.rzh12.notevino.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WineDetailsResponse {
    private Integer wineId;
    private String name;
    private String region;
    private String type;
    private Integer vintage;
    private String imageUrl;
    private LocalDateTime createdAt;
    private List<NoteResponse> notes;  // Use NoteResponse interface
}

