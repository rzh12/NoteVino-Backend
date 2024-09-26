package com.rzh12.notevino.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rzh12.notevino.dto.UserDetailDTO;
import com.rzh12.notevino.dto.UserSigninRequest;
import com.rzh12.notevino.dto.UserSignupRequest;
import com.rzh12.notevino.dto.UserResponse;
import com.rzh12.notevino.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 用戶註冊 API，註冊成功後返回 JWT Token
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(
            @RequestParam("user") String userDataString,  // 接收 JSON 格式的資料
            @RequestParam(value = "picture", required = false) MultipartFile picture) throws IOException {
        try {
            // 將 JSON 字符串轉換為 UserSignupRequest 對象
            ObjectMapper objectMapper = new ObjectMapper();
            UserSignupRequest userRequest = objectMapper.readValue(userDataString, UserSignupRequest.class);

            // 註冊成功後返回 JWT token
            String token = userService.signUp(userRequest, picture);

            // 返回 token
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // 返回結構化的錯誤信息
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    // 用戶登入 API，登入成功後返回 JWT Token
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody UserSigninRequest signinRequest) {
        try {
            // 登入成功後返回 JWT token
            String token = userService.signin(signinRequest);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            // 返回結構化的錯誤信息
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
    }

    // 根據用戶 email 查詢用戶信息
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        try {
            // 從 SecurityContext 中獲取當前已驗證的用戶
            UserDetailDTO currentUser = (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            // 使用當前用戶的 email 查詢資料
            UserResponse userResponse = userService.findUserByEmail(currentUser.getEmail());
            return new ResponseEntity<>(userResponse, HttpStatus.OK);
        } catch (RuntimeException e) {
            // 返回結構化的錯誤信息
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "User not found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    // 新增上傳大頭貼 API
    @PostMapping("/upload/profile-picture")
    public ResponseEntity<?> uploadProfilePicture(@RequestParam("file") MultipartFile file) {
        try {
            // 更新用戶的圖片並返回圖片的 URL
            String imageUrl = userService.updateProfilePicture(file);
            return new ResponseEntity<>(Map.of("imageUrl", imageUrl), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
