package com.realestate.app.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.app.entity.Notification;
import com.realestate.app.repository.NotificationRepository;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(
            NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // 1. Create notification
    public Notification createNotification(
            Long userId,
            String type,
            String title,
            String message,
            Long referenceId) {

        Notification notification = new Notification();

        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setReferenceId(referenceId);
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    // 2. Get all notifications
    public List<Notification> getAllNotifications(Long userId) {
        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId);
    }

    // 3. Get unread notifications
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository
                .findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }

    // 4. Mark one notification as read
    @Transactional
    public Notification markAsRead(Long notificationId, Long userId) {

        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        // Prevent marking another user's notification as read
        if (!userId.equals(notification.getUserId())) {
            throw new RuntimeException(
                    "You cannot access this notification");
        }

        notification.setIsRead(true);

        return notificationRepository.save(notification);
    }

    // 5. Mark all notifications as read
    @Transactional
    public void markAllAsRead(Long userId) {

        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(
                userId);

        for (Notification notification : notifications) {
            notification.setIsRead(true);
        }

        notificationRepository.saveAll(notifications);
    }

    // 6. Count unread notifications
    public long countUnreadNotifications(Long userId) {
        return notificationRepository
                .countByUserIdAndIsReadFalse(userId);
    }

    // 7. Delete notification
    public void deleteNotification(Long notificationId, Long userId) {

        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!userId.equals(notification.getUserId())) {
            throw new RuntimeException(
                    "You cannot delete this notification");
        }

        notificationRepository.delete(notification);
    }
}