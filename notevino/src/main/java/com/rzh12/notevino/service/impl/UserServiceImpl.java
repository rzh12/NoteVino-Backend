package com.rzh12.notevino.service.impl;

import com.rzh12.notevino.dto.UserSigninRequest;
import com.rzh12.notevino.dto.UserSignupRequest;
import com.rzh12.notevino.dto.UserResponse;
import com.rzh12.notevino.model.User;
import com.rzh12.notevino.repository.UserRepository;
import com.rzh12.notevino.security.JwtUtil;
import com.rzh12.notevino.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public String signUp(UserSignupRequest userRequest) {
        // 檢查電子郵件是否已經被註冊
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        // 創建用戶實體，並加密密碼
        User user = new User();
        user.setProvider("LOCAL");
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(userRequest.getPassword()));  // 密碼加密

        // 設置 picture，如果未提供則設置為 null
        user.setPicture(userRequest.getPicture() != null ? userRequest.getPicture() : null);

        // 保存用戶到資料庫
        userRepository.save(user);

        // 註冊成功後，生成 JWT，並返回 token
        return generateJwtToken(user);
    }

    @Override
    public UserResponse findUserByEmail(String email) {
        // 根據 email 查詢用戶
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // 返回用戶資料（不返回密碼）
        return new UserResponse(
                user.getUserId(),
                user.getProvider(),
                user.getUsername(),
                user.getEmail(),
                user.getPicture()
        );
    }

    @Override
    public String signin(UserSigninRequest signinRequest) {
        // 根據 email 查詢用戶
        User user = userRepository.findByEmail(signinRequest.getEmail());

        if (user == null) {
            throw new RuntimeException("Invalid email or password");
        }

        // 驗證密碼
        if (!passwordEncoder.matches(signinRequest.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        // 如果 email 和密碼驗證成功，生成 JWT
        return generateJwtToken(user);
    }

    // private method: 用來生成 JWT
    private String generateJwtToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("email", user.getEmail());
        claims.put("username", user.getUsername());
        claims.put("provider", user.getProvider());

        // 返回生成的 JWT token
        return jwtUtil.generateToken(claims, user.getEmail());
    }
}

