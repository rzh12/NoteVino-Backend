package com.rzh12.notevino.service.impl;

import com.rzh12.notevino.dto.UserDetailDTO;
import com.rzh12.notevino.dto.UserSigninRequest;
import com.rzh12.notevino.dto.UserSignupRequest;
import com.rzh12.notevino.dto.UserResponse;
import com.rzh12.notevino.model.User;
import com.rzh12.notevino.repository.UserRepository;
import com.rzh12.notevino.security.JwtUtil;
import com.rzh12.notevino.service.S3Service;
import com.rzh12.notevino.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private JwtUtil jwtUtil;

    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public String signUp(UserSignupRequest userRequest, MultipartFile picture) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        User user = new User();
        user.setProvider("LOCAL");
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(userRequest.getPassword()));

        if (picture != null && !picture.isEmpty()) {
            String imageUrl = s3Service.uploadFile(picture, "avatar");
            user.setPicture(imageUrl);
        }

        userRepository.save(user);

        logger.info("Saved user with info: {}", user);

        return generateJwtToken(user);
    }

    @Override
    public UserResponse findUserByEmail(String email) {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

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

        User user = userRepository.findByEmail(signinRequest.getEmail());

        if (user == null) {
            throw new RuntimeException("Invalid email or password");
        }

        if (!passwordEncoder.matches(signinRequest.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        return generateJwtToken(user);
    }

    @Override
    public String updateAvatar(MultipartFile file) {

        Integer userId = getCurrentUserId();

        if (file != null && !file.isEmpty()) {
            String imageUrl = s3Service.uploadFile(file, "avatar");

            userRepository.updateUserAvatar(userId, imageUrl);

            return imageUrl;
        } else {
            throw new RuntimeException("Invalid file");
        }
    }

    private String generateJwtToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("email", user.getEmail());
        claims.put("username", user.getUsername());
        claims.put("provider", user.getProvider());

        return jwtUtil.generateToken(claims, user.getEmail());
    }

    private Integer getCurrentUserId() {
        UserDetailDTO currentUser = (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentUser.getUserId();
    }

}

