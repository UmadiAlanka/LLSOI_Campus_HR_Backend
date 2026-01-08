package com.klef.fsad.sdp.repository;

import com.klef.fsad.sdp.model.Attendance;
import com.klef.fsad.sdp.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByEmployeeAndDate(Employee employee, LocalDate date);

    List<Attendance> findByEmployee(Employee employee);

    List<Attendance> findByEmployeeAndDateBetween(Employee employee, LocalDate startDate, LocalDate endDate);

    List<Attendance> findByDate(LocalDate date);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.employee = :employee " +
            "AND YEAR(a.date) = :year AND MONTH(a.date) = :month AND a.status = 'PRESENT'")
    int countWorkingDays(@Param("employee") Employee employee,
                         @Param("year") int year,
                         @Param("month") int month);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.employee = :employee " +
            "AND YEAR(a.date) = :year AND MONTH(a.date) = :month AND a.status = 'LEAVE'")
    int countLeaves(@Param("employee") Employee employee,
                    @Param("year") int year,
                    @Param("month") int month);
}