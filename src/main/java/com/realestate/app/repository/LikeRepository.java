package com.realestate.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.realestate.app.entity.Like;
@Repository 
public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserIdAndPropertyId(Long userId, Long propertyId);

    void deleteByUserIdAndPropertyId(Long userId, Long propertyId);

    List<Like> findByUserId(Long userId);

    List<Like> findByPropertyId(Long propertyId);

    long countByPropertyId(Long propertyId);
}
