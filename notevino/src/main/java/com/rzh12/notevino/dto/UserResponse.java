package com.rzh12.notevino.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Integer id;
    private String provider;
    private String name;
    private String email;
    private String picture;
    private LocalDateTime createdAt;
}
