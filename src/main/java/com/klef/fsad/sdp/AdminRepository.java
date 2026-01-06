package com.klef.fsad.sdp;

import com.klef.fsad.sdp.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin,Integer> {
}
