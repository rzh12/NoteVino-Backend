package com.rzh12.notevino.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {  // 使用泛型 T 來支援不同類型的 data
    private boolean success;
    private String message;
    private T data;  // 新增 data 字段，使用泛型 T 來支援不同類型的資料
}
