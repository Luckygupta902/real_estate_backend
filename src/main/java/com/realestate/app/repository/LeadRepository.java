package com.realestate.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.app.entity.Lead;

public interface LeadRepository extends JpaRepository<Lead, Long> {
     List<Lead> findByCustomerId(Long customerId);

    List<Lead> findByPropertyId(Long propertyId);

    List<Lead> findByAssignedTo(Long assignedTo);

    List<Lead> findByStatusIgnoreCase(String status);

}
