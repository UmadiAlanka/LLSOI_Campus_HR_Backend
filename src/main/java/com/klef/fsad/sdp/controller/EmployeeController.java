package com.klef.fsad.sdp.controller;

import com.klef.fsad.sdp.dto.EmployeeResponseDTO;
import com.klef.fsad.sdp.model.Employee;
import com.klef.fsad.sdp.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "http://localhost:3000")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // Convert Employee → DTO
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

    // Get all employees (only required fields)
    @GetMapping
    public List<EmployeeResponseDTO> getAllEmployees() {
        return employeeService.getAllEmployees()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Get one employee
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> getEmployeeById(@PathVariable Long id) {
        Optional<Employee> employee = employeeService.getEmployeeById(id);
        return employee
                .map(e -> ResponseEntity.ok(mapToDTO(e)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create employee
    @PostMapping
    public ResponseEntity<EmployeeResponseDTO> createEmployee(@RequestBody Employee employee) {
        Employee saved = employeeService.createEmployee(employee);
        return ResponseEntity.ok(mapToDTO(saved));
    }

    // Update employee
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> updateEmployee(
            @PathVariable Long id,
            @RequestBody Employee employee) {

        Employee updated = employeeService.updateEmployee(id, employee);
        return ResponseEntity.ok(mapToDTO(updated));
    }

    // Delete employee
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
