package com.klef.fsad.sdp.repository;


import com.klef.fsad.sdp.model.HR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface HrRepository extends JpaRepository<HR,Long> {
}
