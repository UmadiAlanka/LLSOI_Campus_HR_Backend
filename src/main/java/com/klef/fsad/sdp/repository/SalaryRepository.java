package com.klef.fsad.sdp.repository;

import com.klef.fsad.sdp.model.Employee;
import com.klef.fsad.sdp.model.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {

    // Find salary by employee, month, and year
    Optional<Salary> findByEmployeeAndMonthAndYear(Employee employee, int month, int year);

    // Find all salaries for an employee
    List<Salary> findByEmployeeOrderByYearDescMonthDesc(Employee employee);

    // Find salaries by month and year
    List<Salary> findByMonthAndYear(int month, int year);

    // Find salaries by status
    List<Salary> findByStatus(String status);

    // Get previous month's salary for an employee
    @Query("SELECT s FROM Salary s WHERE s.employee = :employee " +
            "AND (s.year < :year OR (s.year = :year AND s.month < :month)) " +
            "ORDER BY s.year DESC, s.month DESC")
    List<Salary> findPreviousSalaries(@Param("employee") Employee employee,
                                      @Param("year") int year,
                                      @Param("month") int month);

    // Find latest salary for employee
    Optional<Salary> findFirstByEmployeeOrderByYearDescMonthDesc(Employee employee);
}