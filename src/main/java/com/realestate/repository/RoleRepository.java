package com.realestate.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
