package com.klef.fsad.sdp.controller;

import com.klef.fsad.sdp.dto.LoginRequest;
import com.klef.fsad.sdp.dto.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        LoginResponse response = new LoginResponse();

        // Simple hard-coded authentication
        if ("admin".equals(request.getUsername()) && "admin123".equals(request.getPassword())) {
            response.setSuccess(true);
            response.setMessage("Login successful");
            response.setRole("ADMIN");
            response.setUserId(1L);
            response.setUsername("admin");
            response.setName("Administrator");
            return ResponseEntity.ok(response);
        }
        else if ("hrstaff".equals(request.getUsername()) && "hr123".equals(request.getPassword())) {
            response.setSuccess(true);
            response.setMessage("Login successful");
            response.setRole("HR");
            response.setUserId(2L);
            response.setUsername("hrstaff");
            response.setName("HR Staff");
            return ResponseEntity.ok(response);
        }

        response.setSuccess(false);
        response.setMessage("Invalid username or password");
        return ResponseEntity.status(401).body(response);
    }
}
