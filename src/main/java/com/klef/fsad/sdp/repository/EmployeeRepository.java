package com.klef.fsad.sdp.repository;


import com.klef.fsad.sdp.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {
}
