package com.realestate.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.app.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Get all notifications for a user
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Get unread notifications
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(
            Long userId);

    // Count unread notifications
    long countByUserIdAndIsReadFalse(Long userId);
}
