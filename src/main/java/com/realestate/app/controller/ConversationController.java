package com.realestate.app.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.realestate.app.entity.Conversation;
import com.realestate.app.entity.Message;
import com.realestate.app.service.ConversationService;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(
            ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    private Long currentUserId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    // 1. Start a conversation about a property
    @PostMapping
    public ResponseEntity<Conversation> createConversation(
            @RequestBody Map<String, Long> body,
            Authentication authentication) {

        Long propertyId = body.get("propertyId");

        if (propertyId == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "propertyId is required");
        }

        return ResponseEntity.ok(
                conversationService.createConversation(
                        propertyId,
                        currentUserId(authentication)));
    }

    // 2. Get my conversations
    @GetMapping("/my")
    public ResponseEntity<List<Conversation>> getMyConversations(
            Authentication authentication) {

        return ResponseEntity.ok(
                conversationService.getMyConversations(
                        currentUserId(authentication)));
    }

    // 3. ADMIN gets all conversations
    @GetMapping("/admin/all")
    public ResponseEntity<List<Conversation>> getAllConversations() {
        return ResponseEntity.ok(
                conversationService.getAllConversations());
    }

    // 4. Get a single conversation
    @GetMapping("/{conversationId}")
    public ResponseEntity<Conversation> getConversation(
            @PathVariable Long conversationId,
            Authentication authentication) {

        return ResponseEntity.ok(
                conversationService.getConversation(
                        conversationId,
                        currentUserId(authentication),
                        isAdmin(authentication)));
    }

    // 5. Send a message
    @PostMapping("/{conversationId}/messages")
    public ResponseEntity<Message> sendMessage(
            @PathVariable Long conversationId,
            @RequestBody Map<String, String> body,
            Authentication authentication) {

        return ResponseEntity.ok(
                conversationService.sendMessage(
                        conversationId,
                        currentUserId(authentication),
                        body.get("messageText")));
    }

    // 6. Get messages
    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<List<Message>> getMessages(
            @PathVariable Long conversationId,
            Authentication authentication) {

        return ResponseEntity.ok(
                conversationService.getMessages(
                        conversationId,
                        currentUserId(authentication),
                        isAdmin(authentication)));
    }

    // 7. Mark a message as read
    @PutMapping("/{conversationId}/messages/{messageId}/read")
    public ResponseEntity<Message> markAsRead(
            @PathVariable Long conversationId,
            @PathVariable Long messageId,
            Authentication authentication) {

        return ResponseEntity.ok(
                conversationService.markMessageAsRead(
                        conversationId,
                        messageId,
                        currentUserId(authentication)));
    }

    // 8. Delete a conversation (ADMIN only via SecurityConfig)
    @DeleteMapping("/{conversationId}")
    public ResponseEntity<Void> deleteConversation(
            @PathVariable Long conversationId) {

        conversationService.deleteConversation(conversationId);

        return ResponseEntity.noContent().build();
    }
}
