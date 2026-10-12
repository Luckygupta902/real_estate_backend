package com.realestate.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.realestate.app.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPropertyIdOrderByCreatedAtDesc(Long propertyId);

    List<Comment> findByUserIdOrderByCreatedAtDesc(Long userId);
}
