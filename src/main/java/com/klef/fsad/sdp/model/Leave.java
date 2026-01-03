package com.klef.fsad.sdp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
// ... other imports

@Entity  // <--- MAKE SURE THIS IS HERE
public class Leave {
    @Id
    private Long id;
    // ... rest of your code
}