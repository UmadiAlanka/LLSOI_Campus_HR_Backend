package com.klef.fsad.sdp.service;

import com.klef.fsad.sdp.model.Employee;
import com.klef.fsad.sdp.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    // ---------------- CREATE ----------------
    public Employee createEmployee(Employee employee) {
        // Optional: prevent duplicate employee IDs
        if (employeeRepository.existsByEmployeeId(employee.getEmployeeId())) {
            throw new RuntimeException("Employee ID already exists: " + employee.getEmployeeId());
        }
        return employeeRepository.save(employee);
    }

    // ---------------- READ ALL ----------------
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    // ---------------- READ BY EMPLOYEE ID ----------------
    public Optional<Employee> getEmployeeByEmployeeId(String employeeId) {
        return employeeRepository.findByEmployeeId(employeeId);
    }

    // ---------------- UPDATE ----------------
    public Employee updateEmployee(String employeeId, Employee newData) {
        Employee existing = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() ->
                        new RuntimeException("Employee not found with ID: " + employeeId));

        existing.setName(newData.getName());
        existing.setAddress(newData.getAddress());
        existing.setContactNumber(newData.getContactNumber());
        existing.setRole(newData.getRole());
        existing.setJob(newData.getJob());
        existing.setJobType(newData.getJobType());
        existing.setUsername(newData.getUsername());
        existing.setEmail(newData.getEmail());

        // Only update password if user entered a new one
        if (newData.getPassword() != null && !newData.getPassword().isEmpty()) {
            existing.setPassword(newData.getPassword());
        }

        return employeeRepository.save(existing);
    }

    // ---------------- DELETE ----------------
    public void deleteEmployee(String employeeId) {
        Employee existing = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() ->
                        new RuntimeException("Employee not found with ID: " + employeeId));

        employeeRepository.delete(existing);
    }

    // ---------------- DASHBOARD ----------------
    public long getTotalEmployeeCount() {
        return employeeRepository.count();
    }

    // ---------------- LOGIN ----------------
    public Employee login(String username, String password) {
        Optional<Employee> emp = employeeRepository.findByUsername(username);

        if (emp.isPresent() && emp.get().getPassword().equals(password)) {
            return emp.get();
        }
        return null;
    }
}
