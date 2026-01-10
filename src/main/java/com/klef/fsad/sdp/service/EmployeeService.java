package com.klef.fsad.sdp.service;

import com.klef.fsad.sdp.model.Employee;
import com.klef.fsad.sdp.model.HR;
import com.klef.fsad.sdp.repository.EmployeeRepository;
import com.klef.fsad.sdp.repository.HrRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private HrRepository hrRepository;

    // Create new employee
    public Employee createEmployee(Employee employee) {
        // Check if employee ID already exists
        if (employeeRepository.existsById(employee.getId())) {
            throw new RuntimeException("Employee ID already exists");
        }
        return employeeRepository.save(employee);
    }

    // Get all employees
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    // Get employee by ID
    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    // Update employee
    public Employee updateEmployee(Long id, Employee employeeDetails) {
        Optional<Employee> empOpt = employeeRepository.findById(id);
        if (empOpt.isEmpty()) {
            throw new RuntimeException("Employee not found");
        }

        Employee employee = empOpt.get();
        employee.setName(employeeDetails.getName());
        employee.setGender(employeeDetails.getGender());
        employee.setAge(employeeDetails.getAge());
        employee.setDesignation(employeeDetails.getDesignation());
        employee.setDepartment(employeeDetails.getDepartment());
        employee.setSalary(employeeDetails.getSalary());
        employee.setEmail(employeeDetails.getEmail());
        employee.setContact(employeeDetails.getContact());

        if (employeeDetails.getPassword() != null && !employeeDetails.getPassword().isEmpty()) {
            employee.setPassword(employeeDetails.getPassword());
        }

        return employeeRepository.save(employee);
    }

    // Delete employee
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    // Search employees by name
    public List<Employee> searchEmployeesByName(String name) {
        return employeeRepository.findByNameContainingIgnoreCase(name);
    }

    // Assign HR to employee
    public Employee assignHRToEmployee(Long employeeId, Long hrId) {
        Optional<Employee> empOpt = employeeRepository.findById(employeeId);
        Optional<HR> hrOpt = hrRepository.findById(hrId);

        if (empOpt.isEmpty()) {
            throw new RuntimeException("Employee not found");
        }
        if (hrOpt.isEmpty()) {
            throw new RuntimeException("HR not found");
        }

        Employee employee = empOpt.get();
        employee.setHr(hrOpt.get());

        return employeeRepository.save(employee);
    }

    // Get total employee count
    public long getTotalEmployeeCount() {
        return employeeRepository.count();
    }

    // Get employees by department
    public List<Employee> getEmployeesByDepartment(String department) {
        return employeeRepository.findAll().stream()
                .filter(emp -> emp.getDepartment().equalsIgnoreCase(department))
                .toList();
    }

    // Get employees by designation
    public List<Employee> getEmployeesByDesignation(String designation) {
        return employeeRepository.findAll().stream()
                .filter(emp -> emp.getDesignation().equalsIgnoreCase(designation))
                .toList();
    }

    // Verify employee credentials
    public Optional<Employee> verifyCredentials(String username, String password) {
        return employeeRepository.findAll().stream()
                .filter(emp -> emp.getUsername().equals(username) &&
                        emp.getPassword().equals(password))
                .findFirst();
    }

    // Get employee by username
    public Optional<Employee> getEmployeeByUsername(String username) {
        return employeeRepository.findAll().stream()
                .filter(emp -> emp.getUsername().equals(username))
                .findFirst();
    }

    // Get employee by email
    public Optional<Employee> getEmployeeByEmail(String email) {
        return employeeRepository.findAll().stream()
                .filter(emp -> emp.getEmail().equals(email))
                .findFirst();
    }

    // Check if username exists
    public boolean existsByUsername(String username) {
        return employeeRepository.findAll().stream()
                .anyMatch(emp -> emp.getUsername().equals(username));
    }

    // Check if email exists
    public boolean existsByEmail(String email) {
        return employeeRepository.findAll().stream()
                .anyMatch(emp -> emp.getEmail().equals(email));
    }
}