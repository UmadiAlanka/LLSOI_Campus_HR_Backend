package com.klef.fsad.sdp.repository;

import com.klef.fsad.sdp.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    // Existing methods
    Optional<Employee> findByUsername(String username);
    Optional<Employee> findByEmployeeId(String employeeId);
    boolean existsByEmployeeId(String employeeId);

    // --- NEW METHODS FOR FORGOT PASSWORD ---

    /**
     * Finds an employee by their email address.
     * Required for Step 1: Generating the reset link.
     */
    Employee findByEmail(String email);

    /**
     * Finds an employee by their active reset token.
     * Required for Step 2: Validating the link and updating the password.
     */
    Employee findByResetToken(String resetToken);
}