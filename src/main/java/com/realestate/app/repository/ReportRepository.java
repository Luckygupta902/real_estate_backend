package com.realestate.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.realestate.app.entity.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

}
