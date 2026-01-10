package com.klef.fsad.sdp.controller;

import com.klef.fsad.sdp.dto.ApiResponse;
import com.klef.fsad.sdp.model.Attendance;
import com.klef.fsad.sdp.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "*")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping("/clock-in")
    public ResponseEntity<ApiResponse<Attendance>> clockIn(
            @RequestParam Long employeeId,
            @RequestParam String markedBy) {
        try {
            Attendance attendance = attendanceService.clockIn(employeeId, markedBy);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Clocked in successfully", attendance)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage()));
        }
    }

    @PostMapping("/clock-out/{employeeId}")
    public ResponseEntity<ApiResponse<Attendance>> clockOut(@PathVariable Long employeeId) {
        try {
            Attendance attendance = attendanceService.clockOut(employeeId);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Clocked out successfully", attendance)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage()));
        }
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse<List<Attendance>>> getEmployeeAttendance(
            @PathVariable Long employeeId) {
        try {
            List<Attendance> attendance = attendanceService.getEmployeeAttendance(employeeId);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Attendance retrieved", attendance)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error: " + e.getMessage()));
        }
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<ApiResponse<List<Attendance>>> getAttendanceByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<Attendance> attendance = attendanceService.getAttendanceByDate(date);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Attendance retrieved", attendance)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Attendance>> updateAttendance(
            @PathVariable Long id,
            @RequestBody Attendance attendance,
            @RequestParam String updatedBy) {
        try {
            Attendance updated = attendanceService.updateAttendance(id, attendance, updatedBy);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Attendance updated successfully", updated)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage()));
        }
    }
}
