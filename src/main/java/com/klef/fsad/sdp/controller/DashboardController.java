package com.klef.fsad.sdp.controller;

import com.klef.fsad.sdp.dto.ApiResponse;
import com.klef.fsad.sdp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private SalaryService salaryService;

    @Autowired
    private SalaryAnomalyService anomalyService;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private HRService hrService;

    // Get Admin Dashboard Statistics
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAdminDashboard() {
        Map<String, Object> stats = new HashMap<>();

        // Total counts
        stats.put("totalEmployees", employeeService.getTotalEmployeeCount());
        stats.put("totalAdmins", adminService.getAllAdmins().size());
        stats.put("totalHRStaff", hrService.getAllHR().size());
        stats.put("totalLeaveRequests", leaveService.getAllLeaves().size());
        stats.put("pendingLeaveRequests", leaveService.getPendingLeaves().size());

        // Salary statistics
        int currentMonth = LocalDate.now().getMonthValue();
        int currentYear = LocalDate.now().getYear();
        stats.put("currentMonthSalaries", salaryService.getSalariesByMonthAndYear(currentMonth, currentYear).size());
        stats.put("pendingSalaries", salaryService.getSalariesByStatus("PENDING").size());
        stats.put("approvedSalaries", salaryService.getSalariesByStatus("APPROVED").size());

        // Anomaly statistics
        stats.put("totalAnomalies", anomalyService.getAllAnomalies().size());
        stats.put("pendingAnomalies", anomalyService.getPendingAnomalies().size());
        stats.put("criticalAnomalies", anomalyService.getCriticalAnomalies().size());

        // Attendance statistics for today
        stats.put("todayAttendance", attendanceService.getAttendanceByDate(LocalDate.now()).size());

        return ResponseEntity.ok(new ApiResponse<>(true, "Admin dashboard data retrieved", stats));
    }

    // Get HR Dashboard Statistics
    @GetMapping("/hr")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getHRDashboard() {
        Map<String, Object> stats = new HashMap<>();

        // Employee and leave stats
        stats.put("totalEmployees", employeeService.getTotalEmployeeCount());
        stats.put("pendingLeaveRequests", leaveService.getPendingLeaves().size());
        stats.put("approvedLeaves", leaveService.getApprovedLeaves().size());

        // Salary stats
        int currentMonth = LocalDate.now().getMonthValue();
        int currentYear = LocalDate.now().getYear();
        stats.put("currentMonthSalaries", salaryService.getSalariesByMonthAndYear(currentMonth, currentYear).size());
        stats.put("pendingSalaries", salaryService.getSalariesByStatus("PENDING").size());

        // Anomaly stats
        stats.put("pendingAnomalies", anomalyService.getPendingAnomalies().size());
        stats.put("criticalAnomalies", anomalyService.getCriticalAnomalies().size());

        // Today's attendance
        stats.put("todayAttendance", attendanceService.getAttendanceByDate(LocalDate.now()).size());

        return ResponseEntity.ok(new ApiResponse<>(true, "HR dashboard data retrieved", stats));
    }

    // Get Employee Dashboard Statistics
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getEmployeeDashboard(@PathVariable Long employeeId) {
        try {
            Map<String, Object> stats = new HashMap<>();

            // Leave stats
            stats.put("totalLeaveRequests", leaveService.getLeavesByEmployee(employeeId).size());
            stats.put("pendingLeaves", leaveService.getLeavesByEmployee(employeeId).stream()
                    .filter(leave -> "PENDING".equals(leave.getStatus()))
                    .count());
            stats.put("approvedLeaves", leaveService.getLeavesByEmployee(employeeId).stream()
                    .filter(leave -> "APPROVED".equals(leave.getStatus()))
                    .count());

            // Salary stats
            stats.put("salaryHistory", salaryService.getEmployeeSalaries(employeeId).size());

            // Attendance stats
            stats.put("totalAttendance", attendanceService.getEmployeeAttendance(employeeId).size());

            // Today's attendance status
            var todayAttendance = attendanceService.getAttendanceByEmployeeAndDate(employeeId, LocalDate.now());
            stats.put("todayAttendanceMarked", todayAttendance.isPresent());
            if (todayAttendance.isPresent()) {
                stats.put("todayAttendanceStatus", todayAttendance.get().getStatus());
                stats.put("clockedIn", todayAttendance.get().getClockInTime() != null);
                stats.put("clockedOut", todayAttendance.get().getClockOutTime() != null);
            }

            return ResponseEntity.ok(new ApiResponse<>(true, "Employee dashboard data retrieved", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Error retrieving dashboard data: " + e.getMessage()));
        }
    }

    // Get monthly statistics
    @GetMapping("/monthly")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMonthlyStats(
            @RequestParam int month,
            @RequestParam int year) {
        Map<String, Object> stats = new HashMap<>();

        // Salary stats
        var salaries = salaryService.getSalariesByMonthAndYear(month, year);
        stats.put("totalSalaries", salaries.size());
        stats.put("totalPayroll", salaries.stream()
                .mapToDouble(s -> s.getNetSalary())
                .sum());

        return ResponseEntity.ok(new ApiResponse<>(true, "Monthly statistics retrieved", stats));
    }
}