package com.klef.fsad.sdp.controller;

import com.klef.fsad.sdp.dto.EmployeeResponseDTO;
import com.klef.fsad.sdp.model.Employee;
import com.klef.fsad.sdp.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "http://localhost:3000")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // Convert Entity -> DTO
    private EmployeeResponseDTO mapToDTO(Employee emp) {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setEmployeeId(emp.getEmployeeId());
        dto.setName(emp.getName());
        dto.setUsername(emp.getUsername());
        dto.setEmail(emp.getEmail());
        dto.setRole(emp.getRole());
        dto.setJob(emp.getJob());
        dto.setJobType(emp.getJobType());
        dto.setContactNumber(emp.getContactNumber());
        return dto;
    }

    // ================== GET ALL EMPLOYEES ==================
    @GetMapping
    public ResponseEntity<?> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();

        List<EmployeeResponseDTO> dtoList = employees.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Employees fetched successfully",
                "data", dtoList
        ));
    }

    // ================== GET ONE EMPLOYEE ==================
    @GetMapping("/{employeeId}")
    public ResponseEntity<?> getEmployeeByEmployeeId(@PathVariable String employeeId) {
        Optional<Employee> employee = employeeService.getEmployeeByEmployeeId(employeeId);

        if (employee.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of(
                    "success", false,
                    "message", "Employee not found"
            ));
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", mapToDTO(employee.get())
        ));
    }

    // ================== CREATE EMPLOYEE ==================
    @PostMapping
    public ResponseEntity<?> createEmployee(@RequestBody Employee employee) {
        Employee saved = employeeService.createEmployee(employee);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Employee created successfully",
                "data", mapToDTO(saved)
        ));
    }

    // ================== UPDATE EMPLOYEE ==================
    @PutMapping("/{employeeId}")
    public ResponseEntity<?> updateEmployee(
            @PathVariable String employeeId,
            @RequestBody Employee employee) {

        Employee updated = employeeService.updateEmployee(employeeId, employee);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Employee updated successfully",
                "data", mapToDTO(updated)
        ));
    }

    // ================== DELETE EMPLOYEE ==================
    @DeleteMapping("/{employeeId}")
    public ResponseEntity<?> deleteEmployee(@PathVariable String employeeId) {
        employeeService.deleteEmployee(employeeId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Employee deleted successfully"
        ));
    }
}
