package com.klef.fsad.sdp.service;

import com.klef.fsad.sdp.model.Employee;
import com.klef.fsad.sdp.model.Leave;
import com.klef.fsad.sdp.repository.EmployeeRepository;
import com.klef.fsad.sdp.repository.LeaveReposiroty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LeaveService {

    @Autowired
    private LeaveReposiroty leaveRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    // Request leave (Employee)
    public Leave requestLeave(Long employeeId, Leave leave) {
        Optional<Employee> empOpt = employeeRepository.findById(employeeId);
        if (empOpt.isEmpty()) {
            throw new RuntimeException("Employee not found");
        }

        Employee employee = empOpt.get();
        leave.setEmployee(employee);
        leave.setStatus("PENDING");

        return leaveRepository.save(leave);
    }

    // Get all leave requests
    public List<Leave> getAllLeaves() {
        return leaveRepository.findAll();
    }

    // Get leave by ID
    public Optional<Leave> getLeaveById(int id) {
        return leaveRepository.findById(id);
    }

    // Get leaves by employee
    public List<Leave> getLeavesByEmployee(Long employeeId) {
        Optional<Employee> empOpt = employeeRepository.findById(employeeId);
        if (empOpt.isEmpty()) {
            throw new RuntimeException("Employee not found");
        }

        Employee employee = empOpt.get();
        return employee.getLeave();
    }

    // Approve leave (Admin/HR)
    public Leave approveLeave(int leaveId) {
        Optional<Leave> leaveOpt = leaveRepository.findById(leaveId);
        if (leaveOpt.isEmpty()) {
            throw new RuntimeException("Leave request not found");
        }

        Leave leave = leaveOpt.get();
        leave.setStatus("APPROVED");
        return leaveRepository.save(leave);
    }

    // Reject leave (Admin/HR)
    public Leave rejectLeave(int leaveId) {
        Optional<Leave> leaveOpt = leaveRepository.findById(leaveId);
        if (leaveOpt.isEmpty()) {
            throw new RuntimeException("Leave request not found");
        }

        Leave leave = leaveOpt.get();
        leave.setStatus("REJECTED");
        return leaveRepository.save(leave);
    }

    // Get pending leaves
    public List<Leave> getPendingLeaves() {
        return leaveRepository.findAll().stream()
                .filter(leave -> "PENDING".equals(leave.getStatus()))
                .toList();
    }

    // Get approved leaves
    public List<Leave> getApprovedLeaves() {
        return leaveRepository.findAll().stream()
                .filter(leave -> "APPROVED".equals(leave.getStatus()))
                .toList();
    }

    // Delete leave request
    public void deleteLeave(int leaveId) {
        leaveRepository.deleteById(leaveId);
    }

    // Calculate total leave days between dates
    public long calculateLeaveDays(LocalDate startDate, LocalDate endDate) {
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }
}