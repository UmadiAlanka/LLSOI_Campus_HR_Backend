package com.klef.fsad.sdp.service;

import com.klef.fsad.sdp.model.Employee;
import com.klef.fsad.sdp.model.Salary;
import com.klef.fsad.sdp.repository.EmployeeRepository;
import com.klef.fsad.sdp.repository.SalaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class SalaryService {

    @Autowired
    private SalaryRepository salaryRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private SalaryAnomalyService salaryAnomalyService;

    // Generate salary for an employee for a specific month
    public Salary generateSalary(Long employeeId, int month, int year, String generatedBy) {
        Optional<Employee> empOpt = employeeRepository.findById(employeeId);
        if (empOpt.isEmpty()) {
            throw new RuntimeException("Employee not found");
        }

        Employee employee = empOpt.get();

        // Check if salary already exists for this month
        Optional<Salary> existingSalary = salaryRepository.findByEmployeeAndMonthAndYear(employee, month, year);
        if (existingSalary.isPresent()) {
            throw new RuntimeException("Salary already generated for this month");
        }

        Salary salary = new Salary();
        salary.setEmployee(employee);
        salary.setMonth(month);
        salary.setYear(year);
        salary.setBasicSalary(employee.getSalary());

        // Get attendance data
        int workingDays = attendanceService.getWorkingDaysCount(employeeId, year, month);
        int leaveDays = attendanceService.getLeaveCount(employeeId, year, month);

        salary.setTotalDaysWorked(workingDays);
        salary.setTotalLeaves(leaveDays);

        // Calculate allowances (example: 10% of basic salary)
        double allowances = salary.getBasicSalary() * 0.10;
        salary.setAllowances(allowances);

        // Calculate overtime pay (if any) - can be customized
        salary.setOvertimePay(0.0);

        // Calculate gross salary
        double grossSalary = salary.getBasicSalary() + allowances + salary.getOvertimePay();
        salary.setGrossSalary(grossSalary);

        // Calculate deductions based on Sri Lankan standards
        // EPF: 8% of gross salary (employee contribution)
        double epfDeduction = grossSalary * 0.08;
        salary.setEpfDeduction(epfDeduction);

        // ETF: 3% of gross salary (employee contribution)
        double etfDeduction = grossSalary * 0.03;
        salary.setEtfDeduction(etfDeduction);

        salary.setOtherDeductions(0.0);

        // Calculate net salary
        double netSalary = grossSalary - epfDeduction - etfDeduction - salary.getOtherDeductions();
        salary.setNetSalary(netSalary);

        salary.setGeneratedBy(generatedBy);
        salary.setGeneratedDate(LocalDate.now());
        salary.setStatus("PENDING");

        // Save salary
        Salary savedSalary = salaryRepository.save(salary);

        // Check for anomalies
        salaryAnomalyService.detectAnomalies(savedSalary);

        return savedSalary;
    }

    // Generate salary for all employees for a specific month
    public List<Salary> generateSalaryForAllEmployees(int month, int year, String generatedBy) {
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream()
                .map(emp -> {
                    try {
                        return generateSalary(emp.getId(), month, year, generatedBy);
                    } catch (Exception e) {
                        System.err.println("Error generating salary for employee " + emp.getId() + ": " + e.getMessage());
                        return null;
                    }
                })
                .filter(salary -> salary != null)
                .toList();
    }

    // Get salary by ID
    public Optional<Salary> getSalaryById(Long id) {
        return salaryRepository.findById(id);
    }

    // Get all salaries for an employee
    public List<Salary> getEmployeeSalaries(Long employeeId) {
        Optional<Employee> empOpt = employeeRepository.findById(employeeId);
        if (empOpt.isEmpty()) {
            throw new RuntimeException("Employee not found");
        }
        return salaryRepository.findByEmployeeOrderByYearDescMonthDesc(empOpt.get());
    }

    // Get salaries by month and year
    public List<Salary> getSalariesByMonthAndYear(int month, int year) {
        return salaryRepository.findByMonthAndYear(month, year);
    }

    // Get salaries by status
    public List<Salary> getSalariesByStatus(String status) {
        return salaryRepository.findByStatus(status);
    }

    // Update salary (Admin only)
    public Salary updateSalary(Long salaryId, Salary salaryDetails, String updatedBy) {
        Optional<Salary> salaryOpt = salaryRepository.findById(salaryId);
        if (salaryOpt.isEmpty()) {
            throw new RuntimeException("Salary record not found");
        }

        Salary salary = salaryOpt.get();
        salary.setBasicSalary(salaryDetails.getBasicSalary());
        salary.setAllowances(salaryDetails.getAllowances());
        salary.setOvertimePay(salaryDetails.getOvertimePay());
        salary.setEpfDeduction(salaryDetails.getEpfDeduction());
        salary.setEtfDeduction(salaryDetails.getEtfDeduction());
        salary.setOtherDeductions(salaryDetails.getOtherDeductions());

        // Recalculate gross and net salary
        double grossSalary = salary.getBasicSalary() + salary.getAllowances() + salary.getOvertimePay();
        salary.setGrossSalary(grossSalary);

        double netSalary = grossSalary - salary.getEpfDeduction() - salary.getEtfDeduction() - salary.getOtherDeductions();
        salary.setNetSalary(netSalary);

        Salary updatedSalary = salaryRepository.save(salary);

        // Re-check for anomalies after update
        salaryAnomalyService.detectAnomalies(updatedSalary);

        return updatedSalary;
    }

    // Approve salary
    public Salary approveSalary(Long salaryId, String approvedBy) {
        Optional<Salary> salaryOpt = salaryRepository.findById(salaryId);
        if (salaryOpt.isEmpty()) {
            throw new RuntimeException("Salary record not found");
        }

        Salary salary = salaryOpt.get();
        salary.setStatus("APPROVED");
        salary.setApprovedBy(approvedBy);
        salary.setApprovedDate(LocalDate.now());

        return salaryRepository.save(salary);
    }

    // Delete salary
    public void deleteSalary(Long salaryId) {
        salaryRepository.deleteById(salaryId);
    }
}