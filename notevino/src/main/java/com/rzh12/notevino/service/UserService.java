package com.rzh12.notevino.service;

import com.rzh12.notevino.dto.UserSigninRequest;
import com.rzh12.notevino.dto.UserSignupRequest;
import com.rzh12.notevino.dto.UserResponse;

public interface UserService {
    // 用戶註冊，返回 JWT token
    String signUp(UserSignupRequest userRequest);

    // 用戶登入，返回 JWT token
    String signin(UserSigninRequest signinRequest);

    // 根據 email 查詢用戶信息
    UserResponse findUserByEmail(String email);
}
