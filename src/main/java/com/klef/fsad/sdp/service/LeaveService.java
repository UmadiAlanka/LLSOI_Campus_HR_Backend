package com.klef.fsad.sdp.service;

import com.klef.fsad.sdp.model.Employee;
import com.klef.fsad.sdp.model.Leave;
import com.klef.fsad.sdp.repository.EmployeeRepository;
import com.klef.fsad.sdp.repository.LeaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaveService {

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    // Get all leaves
    public List<Leave> getAllLeaves() {
        return leaveRepository.findAll();
    }

    // Get all pending leaves
    public List<Leave> getPendingLeaves() {
        return leaveRepository.findByStatus("PENDING");
    }

    // Get all approved leaves
    public List<Leave> getApprovedLeaves() {
        return leaveRepository.findByStatus("APPROVED");
    }

    // Request a new leave
    public Leave requestLeave(Long employeeId, Leave leave) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        leave.setEmployee(employee);
        leave.setStatus("PENDING");
        return leaveRepository.save(leave);
    }

    // Get leaves by employee
    public List<Leave> getLeavesByEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return leaveRepository.findByEmployee(employee);
    }

    // Approve leave
    public Leave approveLeave(int id) {
        Leave leave = leaveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found"));
        leave.setStatus("APPROVED");
        return leaveRepository.save(leave);
    }

    // Reject leave
    public Leave rejectLeave(int id) {
        Leave leave = leaveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found"));
        leave.setStatus("REJECTED");
        return leaveRepository.save(leave);
    }
}

