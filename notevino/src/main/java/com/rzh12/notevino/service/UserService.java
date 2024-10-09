package com.rzh12.notevino.service;

import com.rzh12.notevino.dto.UserSigninRequest;
import com.rzh12.notevino.dto.UserSignupRequest;
import com.rzh12.notevino.dto.UserResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    String signUp(UserSignupRequest userRequest, MultipartFile picture);

    String signin(UserSigninRequest signinRequest);

    UserResponse findUserByEmail(String email);

    String updateAvatar(MultipartFile file);

}
