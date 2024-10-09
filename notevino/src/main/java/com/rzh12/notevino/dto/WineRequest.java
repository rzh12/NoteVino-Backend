package com.rzh12.notevino.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WineRequest {
    private Integer userId;
    private String name;
    private String region;
    private String type;
    private Integer vintage;
    private String imageUrl;
}
