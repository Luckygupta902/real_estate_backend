package com.realestate.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.entity.Like;

public interface LikeRepository extends JpaRepository<Like, Long> {

}
