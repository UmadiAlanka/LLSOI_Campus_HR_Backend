package com.klef.fsad.sdp.service;

import com.klef.fsad.sdp.dto.LoginRequest;
import com.klef.fsad.sdp.dto.LoginResponse;
import com.klef.fsad.sdp.model.Admin;
import com.klef.fsad.sdp.model.Employee;
import com.klef.fsad.sdp.model.HR;
import com.klef.fsad.sdp.repository.AdminRepository;
import com.klef.fsad.sdp.repository.EmployeeRepository;
import com.klef.fsad.sdp.repository.HrRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private HrRepository hrRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public LoginResponse authenticate(LoginRequest request) {
        // Check in Admin table
        Optional<Admin> adminOpt = adminRepository.findAll().stream()
                .filter(admin -> admin.getUsername().equals(request.getUsername())
                        && admin.getPassword().equals(request.getPassword()))
                .findFirst();

        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            return new LoginResponse(
                    true,
                    "Login successful",
                    "ADMIN",
                    (long) admin.getId(),
                    admin.getUsername(),
                    "Administrator"
            );
        }

        // Check in HR table
        Optional<HR> hrOpt = hrRepository.findAll().stream()
                .filter(hr -> hr.getUsername().equals(request.getUsername())
                        && hr.getPassword().equals(request.getPassword()))
                .findFirst();

        if (hrOpt.isPresent()) {
            HR hr = hrOpt.get();
            return new LoginResponse(
                    true,
                    "Login successful",
                    "HR",
                    hr.getId(),
                    hr.getUsername(),
                    hr.getName()
            );
        }

        // Check in Employee table
        Optional<Employee> empOpt = employeeRepository.findAll().stream()
                .filter(emp -> emp.getUsername().equals(request.getUsername())
                        && emp.getPassword().equals(request.getPassword()))
                .findFirst();

        if (empOpt.isPresent()) {
            Employee emp = empOpt.get();
            return new LoginResponse(
                    true,
                    "Login successful",
                    "EMPLOYEE",
                    emp.getId(),
                    emp.getUsername(),
                    emp.getName()
            );
        }

        return new LoginResponse();
    }
}