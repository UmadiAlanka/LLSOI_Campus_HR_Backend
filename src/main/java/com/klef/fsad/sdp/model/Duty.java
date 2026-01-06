package com.klef.fsad.sdp.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Duty {
    @Id
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
