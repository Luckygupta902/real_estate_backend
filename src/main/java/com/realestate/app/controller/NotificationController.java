package com.realestate.app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.realestate.app.entity.Notification;
import com.realestate.app.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // 1. Create notification
    @PostMapping
    public ResponseEntity<Notification> createNotification(
            @RequestBody Notification notification) {

        Notification saved = notificationService.createNotification(
                notification.getUserId(),
                notification.getType(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getReferenceId());

        return ResponseEntity.ok(saved);
    }

    // 2. Get all notifications for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getAllNotifications(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                notificationService.getAllNotifications(userId));
    }

    // 3. Get unread notifications
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                notificationService.getUnreadNotifications(userId));
    }

    // 4. Mark one notification as read
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Notification> markAsRead(
            @PathVariable Long notificationId,
            @RequestParam Long userId) {

        return ResponseEntity.ok(
                notificationService.markAsRead(notificationId, userId));
    }

    // 5. Mark all notifications as read
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<String> markAllAsRead(
            @PathVariable Long userId) {

        notificationService.markAllAsRead(userId);

        return ResponseEntity.ok(
                "All notifications marked as read");
    }

    // 6. Count unread notifications
    @GetMapping("/user/{userId}/unread/count")
    public ResponseEntity<Long> countUnreadNotifications(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                notificationService.countUnreadNotifications(userId));
    }

    // 7. Delete notification
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<String> deleteNotification(
            @PathVariable Long notificationId,
            @RequestParam Long userId) {

        notificationService.deleteNotification(notificationId, userId);

        return ResponseEntity.ok("Notification deleted successfully");
    }
}
