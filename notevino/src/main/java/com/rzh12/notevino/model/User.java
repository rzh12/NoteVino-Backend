package com.rzh12.notevino.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Integer userId;
    private String provider;
    private String username;
    private String email;
    private String passwordHash;
    private String picture;
    private LocalDateTime createdAt;
}