package com.realestate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.realestate.entity.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

}
