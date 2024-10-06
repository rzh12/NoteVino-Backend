package com.rzh12.notevino.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SatNoteRequest {
    private String sweetness;
    private String acidity;
    private String tannin;
    private String alcohol;
    private String body;
    private String flavourIntensity;
    private String finish;
    private String quality;
    private String potentialForAgeing;
}
