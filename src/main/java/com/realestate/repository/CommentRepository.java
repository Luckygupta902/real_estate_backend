package com.realestate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.realestate.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

}
