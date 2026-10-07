package com.realestate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.realestate.entity.LeadActivity;

@Repository
public interface LeadActivityRepository extends JpaRepository<LeadActivity, Long> {

}
