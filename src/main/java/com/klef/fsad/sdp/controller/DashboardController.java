package com.klef.fsad.sdp.controller;

import com.klef.fsad.sdp.dto.ApiResponse;
import com.klef.fsad.sdp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    // ================= ADMIN DASHBOARD =================
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAdminDashboard() {

        Map<String, Object> stats = new HashMap<>();

        stats.put("totalEmployees", employeeService.getTotalEmployeeCount());
        stats.put("totalAdmins", adminService.getAllAdmins().size());
        stats.put("totalHRStaff", hrService.getAllHR().size());
        stats.put("totalLeaveRequests", leaveService.getAllLeaves().size());
        stats.put("pendingLeaveRequests", leaveService.getPendingLeaves().size());

        int currentMonth = LocalDate.now().getMonthValue();
        int currentYear = LocalDate.now().getYear();

        stats.put("currentMonthSalaries",
                salaryService.getSalariesByMonthAndYear(currentMonth, currentYear).size());
        stats.put("pendingSalaries",
                salaryService.getSalariesByStatus("PENDING").size());
        stats.put("approvedSalaries",
                salaryService.getSalariesByStatus("APPROVED").size());

        stats.put("totalAnomalies", anomalyService.getAllAnomalies().size());
        stats.put("pendingAnomalies", anomalyService.getPendingAnomalies().size());
        stats.put("criticalAnomalies", anomalyService.getCriticalAnomalies().size());

        stats.put("todayAttendance",
                attendanceService.getAttendanceByDate(LocalDate.now()).size());

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Admin dashboard data retrieved", stats)
        );
    }

    // ================= HR DASHBOARD =================
    @GetMapping("/hr")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getHRDashboard() {

        Map<String, Object> stats = new HashMap<>();

        stats.put("totalEmployees", employeeService.getTotalEmployeeCount());
        stats.put("pendingLeaveRequests", leaveService.getPendingLeaves().size());
        stats.put("approvedLeaves", leaveService.getApprovedLeaves().size());

        int currentMonth = LocalDate.now().getMonthValue();
        int currentYear = LocalDate.now().getYear();

        stats.put("currentMonthSalaries",
                salaryService.getSalariesByMonthAndYear(currentMonth, currentYear).size());
        stats.put("pendingSalaries",
                salaryService.getSalariesByStatus("PENDING").size());

        stats.put("pendingAnomalies", anomalyService.getPendingAnomalies().size());
        stats.put("criticalAnomalies", anomalyService.getCriticalAnomalies().size());

        stats.put("todayAttendance",
                attendanceService.getAttendanceByDate(LocalDate.now()).size());

        return ResponseEntity.ok(
                new ApiResponse<>(true, "HR dashboard data retrieved", stats)
        );
    }

    // ================= EMPLOYEE DASHBOARD =================
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getEmployeeDashboard(
            @PathVariable String employeeId) {

        try {
            Map<String, Object> stats = new HashMap<>();

            var leaves = leaveService.getLeavesByEmployee(employeeId);

            stats.put("totalLeaveRequests", leaves.size());
            stats.put("pendingLeaves",
                    leaves.stream().filter(l -> "PENDING".equals(l.getStatus())).count());
            stats.put("approvedLeaves",
                    leaves.stream().filter(l -> "APPROVED".equals(l.getStatus())).count());

            stats.put("salaryHistory",
                    salaryService.getEmployeeSalaries(employeeId).size());

            stats.put("totalAttendance",
                    attendanceService.getEmployeeAttendance(employeeId).size());

            Optional<?> todayAttendance =
                    attendanceService.getAttendanceByEmployeeAndDate(employeeId, LocalDate.now());

            stats.put("todayAttendanceMarked", todayAttendance.isPresent());

            if (todayAttendance.isPresent()) {
                var att = todayAttendance.get();
                stats.put("todayAttendanceStatus", ((com.klef.fsad.sdp.model.Attendance) att).getStatus());
                stats.put("clockedIn", ((com.klef.fsad.sdp.model.Attendance) att).getClockInTime() != null);
                stats.put("clockedOut", ((com.klef.fsad.sdp.model.Attendance) att).getClockOutTime() != null);
            }

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Employee dashboard data retrieved", stats)
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, "Error retrieving dashboard data: " + e.getMessage())
            );
        }
    }

    // ================= MONTHLY STATS =================
    @GetMapping("/monthly")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMonthlyStats(
            @RequestParam int month,
            @RequestParam int year) {

        Map<String, Object> stats = new HashMap<>();

        var salaries = salaryService.getSalariesByMonthAndYear(month, year);

        stats.put("totalSalaries", salaries.size());
        stats.put("totalPayroll",
                salaries.stream().mapToDouble(s -> s.getNetSalary()).sum());

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Monthly statistics retrieved", stats)
        );
    }
}
