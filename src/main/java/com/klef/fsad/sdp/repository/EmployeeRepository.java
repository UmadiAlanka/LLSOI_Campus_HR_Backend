package com.klef.fsad.sdp.repository;

import com.klef.fsad.sdp.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    // This method must match the field name in Employee entity: private String username;
    Optional<Employee> findByUsername(String username);
    Optional<Employee> findByEmployeeId(String employeeId);
    boolean existsByEmployeeId(String employeeId);
}
