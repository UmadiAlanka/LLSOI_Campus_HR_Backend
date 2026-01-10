package com.klef.fsad.sdp.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // Simple authentication logic (replace with proper authentication later)
            if ("admin".equals(request.getUsername()) && "admin123".equals(request.getPassword())) {
                LoginResponse response = new LoginResponse();
                response.setSuccess(true);
                response.setMessage("Login successful");
                response.setRole("ADMIN");
                response.setUserId(1L);
                response.setUsername("admin");
                response.setName("Administrator");
                return ResponseEntity.ok(response);
            } else if ("hrstaff".equals(request.getUsername()) && "hr123".equals(request.getPassword())) {
                LoginResponse response = new LoginResponse();
                response.setSuccess(true);
                response.setMessage("Login successful");
                response.setRole("HR");
                response.setUserId(2L);
                response.setUsername("hrstaff");
                response.setName("HR Staff");
                return ResponseEntity.ok(response);
            } else if ("jane.smith".equals(request.getUsername()) && "pass123".equals(request.getPassword())) {
                LoginResponse response = new LoginResponse();
                response.setSuccess(true);
                response.setMessage("Login successful");
                response.setRole("EMPLOYEE");
                response.setUserId(3L);
                response.setUsername("jane.smith");
                response.setName("Jane Smith");
                return ResponseEntity.ok(response);
            }

            LoginResponse response = new LoginResponse();
            response.setSuccess(false);
            response.setMessage("Invalid credentials");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LoginResponse response = new LoginResponse();
            response.setSuccess(false);
            response.setMessage("Login error: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}

// LoginRequest DTO
class LoginRequest {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

// LoginResponse DTO
class LoginResponse {
    private boolean success;
    private String message;
    private String role;
    private Long userId;
    private String username;
    private String name;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}