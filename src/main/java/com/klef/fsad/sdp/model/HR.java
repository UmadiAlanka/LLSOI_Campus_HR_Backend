package com.klef.fsad.sdp.model;

import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;

import java.util.List;

public class HR {
    private Long id;
    private String name;
    private String email;
    private String password;
    private String department;
    private String contact;

    @OneToMany(mappedBy="HR",cascade= CascadeType.ALL)
    private List<Employee> employees;
    private List<Duty>dutiesAssigned;

}
