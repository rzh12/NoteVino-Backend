package com.rzh12.notevino.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WineAutocompleteResponse {
    private Integer wineId;
    private String name;
    private String region;
    private Double score;
}
