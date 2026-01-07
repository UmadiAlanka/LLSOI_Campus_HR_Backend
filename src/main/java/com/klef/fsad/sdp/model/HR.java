package com.klef.fsad.sdp.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "hr_table")
public class HR {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hr_id")
    private Long id;

    @Column(name = "hr_name", nullable = false)
    private String name;

    @Column(name = "hr_email", nullable = false, unique = true)
    private String email;

    @Column(name = "hr_username", nullable = false, unique = true)
    private String username;

    @Column(name = "hr_password", nullable = false)
    private String password;

    @Column(name = "hr_dept", nullable = false)
    private String department;

    @Column(name = "hr_contact", nullable = false, unique = true)
    private String contact;

    // One HR → Many Employees
    @OneToMany(mappedBy = "hr", cascade = CascadeType.ALL)
    private List<Employee> employees;

    // One HR → Many Duties
    @OneToMany(mappedBy = "assignedByHR", cascade = CascadeType.ALL)
    private List<Duty> dutiesAssigned;

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public List<Duty> getDutiesAssigned() {
        return dutiesAssigned;
    }

    public void setDutiesAssigned(List<Duty> dutiesAssigned) {
        this.dutiesAssigned = dutiesAssigned;
    }

    @Override
    public String toString() {
        return "HR{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", department='" + department + '\'' +
                ", contact='" + contact + '\'' +
                '}';
    }
}
