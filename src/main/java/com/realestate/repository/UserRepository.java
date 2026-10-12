package com.realestate.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
