package com.realestate.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.realestate.app.entity.SavedProperty;

@Repository
public interface SavedPropertyRepository extends JpaRepository<SavedProperty, Long> {

}
