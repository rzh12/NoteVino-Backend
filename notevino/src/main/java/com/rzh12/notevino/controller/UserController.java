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

    // User signup API, returns a JWT Token after successful registration
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(
            @RequestParam("user") String userDataString,
            @RequestParam(value = "picture", required = false) MultipartFile picture) throws IOException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UserSignupRequest userRequest = objectMapper.readValue(userDataString, UserSignupRequest.class);

            String token = userService.signUp(userRequest, picture);

            // Return token
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    // User login API, returns a JWT Token upon successful login
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody UserSigninRequest signinRequest) {
        try {
            String token = userService.signin(signinRequest);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
    }

    // Retrieve user information based on the user's email
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        try {
            // Retrieve the currently authenticated user from the SecurityContext
            UserDetailDTO currentUser = (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            UserResponse userResponse = userService.findUserByEmail(currentUser.getEmail());
            return new ResponseEntity<>(userResponse, HttpStatus.OK);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "User not found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    // Create an API to upload profile avatars
    @PostMapping("/upload-avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("picture") MultipartFile picture) {
        try {
            String imageUrl = userService.updateAvatar(picture);
            return new ResponseEntity<>(Map.of("imageUrl", imageUrl), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
