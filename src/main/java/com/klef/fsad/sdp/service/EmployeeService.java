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

    // CREATE
    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);   // NO existsById check
    }

    // READ ALL
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    // READ BY ID
    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    // UPDATE
    public Employee updateEmployee(Long id, Employee employee) {
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        existing.setName(employee.getName());
        existing.setEmployeeId(employee.getEmployeeId());
        existing.setAddress(employee.getAddress());
        existing.setContactNumber(employee.getContactNumber());
        existing.setRole(employee.getRole());
        existing.setJob(employee.getJob());
        existing.setJobType(employee.getJobType());
        existing.setUsername(employee.getUsername());
        existing.setEmail(employee.getEmail());
        existing.setPassword(employee.getPassword());

        return employeeRepository.save(existing);
    }

    // DELETE
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    // FOR DASHBOARD
    public long getTotalEmployeeCount() {
        return employeeRepository.count();
    }
    public Employee login(String username, String password) {
        Optional<Employee> emp = employeeRepository.findByUsername(username);

        if (emp.isPresent()) {
            if (emp.get().getPassword().equals(password)) {
                return emp.get();
            }
        }
        return null;
    }
}
