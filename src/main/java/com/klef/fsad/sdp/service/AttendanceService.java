package com.klef.fsad.sdp.service;

import com.klef.fsad.sdp.model.Attendance;
import com.klef.fsad.sdp.model.Employee;
import com.klef.fsad.sdp.repository.AttendanceRepository;
import com.klef.fsad.sdp.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    // Mark attendance (Clock In)
    public Attendance clockIn(Long employeeId, String markedBy) {
        Optional<Employee> empOpt = employeeRepository.findById(employeeId);
        if (empOpt.isEmpty()) {
            throw new RuntimeException("Employee not found");
        }

        Employee employee = empOpt.get();
        LocalDate today = LocalDate.now();

        // Check if already marked today
        Optional<Attendance> existingAttendance = attendanceRepository.findByEmployeeAndDate(employee, today);
        if (existingAttendance.isPresent()) {
            throw new RuntimeException("Attendance already marked for today");
        }

        Attendance attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setDate(today);
        attendance.setClockInTime(LocalTime.now());
        attendance.setStatus("PRESENT");
        attendance.setMarkedBy(markedBy);
        attendance.setLastModified(LocalDate.now());

        return attendanceRepository.save(attendance);
    }

    // Clock Out
    public Attendance clockOut(Long employeeId) {
        Optional<Employee> empOpt = employeeRepository.findById(employeeId);
        if (empOpt.isEmpty()) {
            throw new RuntimeException("Employee not found");
        }

        Employee employee = empOpt.get();
        LocalDate today = LocalDate.now();

        Optional<Attendance> attendanceOpt = attendanceRepository.findByEmployeeAndDate(employee, today);
        if (attendanceOpt.isEmpty()) {
            throw new RuntimeException("Please clock in first");
        }

        Attendance attendance = attendanceOpt.get();
        if (attendance.getClockOutTime() != null) {
            throw new RuntimeException("Already clocked out for today");
        }

        attendance.setClockOutTime(LocalTime.now());

        // Calculate working hours
        if (attendance.getClockInTime() != null) {
            Duration duration = Duration.between(attendance.getClockInTime(), attendance.getClockOutTime());
            double hours = duration.toMinutes() / 60.0;
            attendance.setWorkingHours(hours);
        }

        attendance.setLastModified(LocalDate.now());
        return attendanceRepository.save(attendance);
    }

    // Get attendance by employee and date
    public Optional<Attendance> getAttendanceByEmployeeAndDate(Long employeeId, LocalDate date) {
        Optional<Employee> empOpt = employeeRepository.findById(employeeId);
        if (empOpt.isEmpty()) {
            return Optional.empty();
        }
        return attendanceRepository.findByEmployeeAndDate(empOpt.get(), date);
    }

    // Get all attendance for an employee
    public List<Attendance> getEmployeeAttendance(Long employeeId) {
        Optional<Employee> empOpt = employeeRepository.findById(employeeId);
        if (empOpt.isEmpty()) {
            throw new RuntimeException("Employee not found");
        }
        return attendanceRepository.findByEmployee(empOpt.get());
    }

    // Get attendance within date range
    public List<Attendance> getAttendanceByDateRange(Long employeeId, LocalDate startDate, LocalDate endDate) {
        Optional<Employee> empOpt = employeeRepository.findById(employeeId);
        if (empOpt.isEmpty()) {
            throw new RuntimeException("Employee not found");
        }
        return attendanceRepository.findByEmployeeAndDateBetween(empOpt.get(), startDate, endDate);
    }

    // Get all attendance for a specific date
    public List<Attendance> getAttendanceByDate(LocalDate date) {
        return attendanceRepository.findByDate(date);
    }

    // Update attendance (Admin/HR only)
    public Attendance updateAttendance(Long attendanceId, Attendance attendanceDetails, String updatedBy) {
        Optional<Attendance> optionalAttendance = attendanceRepository.findById(attendanceId);
        if (optionalAttendance.isEmpty()) {
            throw new RuntimeException("Attendance record not found");
        }

        Attendance attendance = optionalAttendance.get();
        attendance.setStatus(attendanceDetails.getStatus());
        attendance.setClockInTime(attendanceDetails.getClockInTime());
        attendance.setClockOutTime(attendanceDetails.getClockOutTime());
        attendance.setWorkingHours(attendanceDetails.getWorkingHours());
        attendance.setRemarks(attendanceDetails.getRemarks());
        attendance.setMarkedBy(updatedBy);
        attendance.setLastModified(LocalDate.now());

        return attendanceRepository.save(attendance);
    }

    // Get working days count for salary calculation
    public int getWorkingDaysCount(Long employeeId, int year, int month) {
        Optional<Employee> empOpt = employeeRepository.findById(employeeId);
        if (empOpt.isEmpty()) {
            return 0;
        }
        return attendanceRepository.countWorkingDays(empOpt.get(), year, month);
    }

    // Get leave count for salary calculation
    public int getLeaveCount(Long employeeId, int year, int month) {
        Optional<Employee> empOpt = employeeRepository.findById(employeeId);
        if (empOpt.isEmpty()) {
            return 0;
        }
        return attendanceRepository.countLeaves(empOpt.get(), year, month);
    }
}