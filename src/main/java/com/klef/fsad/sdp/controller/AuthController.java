package com.klef.fsad.sdp.controller;

import com.klef.fsad.sdp.dto.LoginRequest;
import com.klef.fsad.sdp.dto.LoginResponse;
import com.klef.fsad.sdp.model.Employee;
import com.klef.fsad.sdp.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        LoginResponse response = new LoginResponse();

        Employee employee = employeeService.login(request.getUsername(), request.getPassword());

        if (employee != null) {
            response.setSuccess(true);
            response.setMessage("Login successful");
            response.setUserId(employee.getEmployeeId());
            response.setUsername(employee.getUsername());
            response.setName(employee.getName());
            response.setRole(employee.getRole());

            return ResponseEntity.ok(response);
        }

        response.setSuccess(false);
        response.setMessage("Invalid username or password");
        return ResponseEntity.status(401).body(response);
    }
}
