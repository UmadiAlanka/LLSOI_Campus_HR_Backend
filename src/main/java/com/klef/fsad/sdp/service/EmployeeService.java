package com.klef.fsad.sdp.service;

import com.klef.fsad.sdp.model.Employee;
import com.klef.fsad.sdp.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private JavaMailSender mailSender;

    // ---------------- FORGOT PASSWORD LOGIC ----------------

    public boolean processForgotPassword(String email) {
        Employee employee = employeeRepository.findByEmail(email);

        if (employee != null) {
            String token = UUID.randomUUID().toString();
            employee.setResetToken(token);
            employee.setTokenExpiryDate(LocalDateTime.now().plusMinutes(15));

            employeeRepository.save(employee);
            sendResetEmail(employee.getEmail(), token);
            return true;
        }
        return false;
    }

    private void sendResetEmail(String email, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("llsoicampus27@gmail.com");
        message.setTo(email);
        message.setSubject("Password Reset Request - LLSOI Campus HR");

        // Note: Using port 3000 for React frontend
        String resetLink = "http://localhost:3000/reset-password?token=" + token;

        message.setText("To reset your password, click the link below:\n" + resetLink +
                "\n\nThis link will expire in 15 minutes.");

        mailSender.send(message);
    }

    public boolean updatePassword(String token, String newPassword) {
        Employee employee = employeeRepository.findByResetToken(token);

        if (employee != null && employee.getTokenExpiryDate().isAfter(LocalDateTime.now())) {
            employee.setPassword(newPassword);
            employee.setResetToken(null);
            employee.setTokenExpiryDate(null);

            employeeRepository.save(employee);
            return true;
        }
        return false;
    }

    // ---------------- EXISTING METHODS ----------------

    public Employee createEmployee(Employee employee) {
        if (employeeRepository.existsByEmployeeId(employee.getEmployeeId())) {
            throw new RuntimeException("Employee ID already exists: " + employee.getEmployeeId());
        }
        return employeeRepository.save(employee);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> getEmployeeByEmployeeId(String employeeId) {
        return employeeRepository.findByEmployeeId(employeeId);
    }

    public Employee updateEmployee(String employeeId, Employee newData) {
        Employee existing = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));

        existing.setName(newData.getName());
        existing.setAddress(newData.getAddress());
        existing.setContactNumber(newData.getContactNumber());
        existing.setRole(newData.getRole());
        existing.setJob(newData.getJob());
        existing.setJobType(newData.getJobType());
        existing.setUsername(newData.getUsername());
        existing.setEmail(newData.getEmail());

        if (newData.getPassword() != null && !newData.getPassword().isEmpty()) {
            existing.setPassword(newData.getPassword());
        }

        return employeeRepository.save(existing);
    }

    public void deleteEmployee(String employeeId) {
        Employee existing = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));
        employeeRepository.delete(existing);
    }

    public long getTotalEmployeeCount() {
        return employeeRepository.count();
    }

    public Employee login(String username, String password) {
        Optional<Employee> emp = employeeRepository.findByUsername(username);
        if (emp.isPresent() && emp.get().getPassword().equals(password)) {
            return emp.get();
        }
        return null;
    }
}