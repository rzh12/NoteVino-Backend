package com.rzh12.notevino.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private List<NoteResponse> notes;  // 使用 NoteResponse interface
}

