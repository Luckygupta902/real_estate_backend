package com.realestate.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.app.entity.PropertyMedia;

public interface PropertyMediaRepository extends JpaRepository<PropertyMedia, Long> {
    List<PropertyMedia> findByPropertyId(Long propertyId);

    void deleteByPropertyId(Long propertyId);
}
