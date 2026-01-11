package com.klef.fsad.sdp.service;

import com.klef.fsad.sdp.model.Salary;
import com.klef.fsad.sdp.repository.SalaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalaryService {

    @Autowired
    private SalaryRepository salaryRepository;

    public List<Salary> getSalariesByMonthAndYear(int month, int year) {
        return salaryRepository.findByMonthAndYear(month, year);
    }

    public List<Salary> getSalariesByStatus(String status) {
        return salaryRepository.findByStatus(status);
    }

    public List<Salary> getEmployeeSalaries(Long employeeId) {
        return salaryRepository.findByEmployeeId(employeeId);
    }
}
