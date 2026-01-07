package com.klef.fsad.sdp.repository;

import com.klef.fsad.sdp.model.Leave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveReposiroty extends JpaRepository<Leave,Integer> {

}
