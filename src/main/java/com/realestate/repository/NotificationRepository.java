package com.realestate.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
