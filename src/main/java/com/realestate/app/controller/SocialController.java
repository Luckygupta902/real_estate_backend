package com.realestate.app.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.realestate.app.entity.Like;
import com.realestate.app.entity.Comment;
import com.realestate.app.entity.Follow;
import com.realestate.app.service.SocialService;

@RestController
@RequestMapping("/api")
public class SocialController {

    private final SocialService socialService;

    public SocialController(SocialService socialService) {
        this.socialService = socialService;
    }

    private Long currentUserId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a ->
                        a.getAuthority().equals("ROLE_ADMIN"));
    }

    // ---------------- LIKES ----------------

    @PostMapping("/properties/{propertyId}/likes")
    public ResponseEntity<Like> likeProperty(
            @PathVariable Long propertyId,
            Authentication authentication) {

        Like like = socialService.likeProperty(
                propertyId, currentUserId(authentication));

        return ResponseEntity.status(HttpStatus.CREATED).body(like);
    }

    @DeleteMapping("/properties/{propertyId}/likes")
    public ResponseEntity<Void> unlikeProperty(
            @PathVariable Long propertyId,
            Authentication authentication) {

        socialService.unlikeProperty(
                propertyId, currentUserId(authentication));

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/likes/my")
    public ResponseEntity<List<Like>> getMyLikes(
            Authentication authentication) {

        return ResponseEntity.ok(
                socialService.getMyLikes(currentUserId(authentication)));
    }

    @GetMapping("/properties/{propertyId}/likes")
    public ResponseEntity<List<Like>> getPropertyLikes(
            @PathVariable Long propertyId) {

        return ResponseEntity.ok(
                socialService.getPropertyLikes(propertyId));
    }

    @GetMapping("/properties/{propertyId}/likes/count")
    public ResponseEntity<Long> countLikes(
            @PathVariable Long propertyId) {

        return ResponseEntity.ok(
                socialService.countPropertyLikes(propertyId));
    }

    // ---------------- COMMENTS ----------------

    @PostMapping("/properties/{propertyId}/comments")
    public ResponseEntity<Comment> addComment(
            @PathVariable Long propertyId,
            @RequestBody Map<String, String> body,
            Authentication authentication) {

        Comment comment = socialService.addComment(
                propertyId,
                currentUserId(authentication),
                body.get("commentText"));

        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    @GetMapping("/properties/{propertyId}/comments")
    public ResponseEntity<List<Comment>> getPropertyComments(
            @PathVariable Long propertyId) {

        return ResponseEntity.ok(
                socialService.getPropertyComments(propertyId));
    }

    @GetMapping("/comments/my")
    public ResponseEntity<List<Comment>> getMyComments(
            Authentication authentication) {

        return ResponseEntity.ok(
                socialService.getMyComments(currentUserId(authentication)));
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<Comment> updateComment(
            @PathVariable Long commentId,
            @RequestBody Map<String, String> body,
            Authentication authentication) {

        Comment comment = socialService.updateComment(
                commentId,
                currentUserId(authentication),
                isAdmin(authentication),
                body.get("commentText"));

        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            Authentication authentication) {

        socialService.deleteComment(
                commentId,
                currentUserId(authentication),
                isAdmin(authentication));

        return ResponseEntity.noContent().build();
    }

    // ---------------- FOLLOWS ----------------

    @PostMapping("/users/{userId}/follow")
    public ResponseEntity<Follow> followUser(
            @PathVariable Long userId,
            Authentication authentication) {

        Follow follow = socialService.followUser(
                userId, currentUserId(authentication));

        return ResponseEntity.status(HttpStatus.CREATED).body(follow);
    }

    @DeleteMapping("/users/{userId}/follow")
    public ResponseEntity<Void> unfollowUser(
            @PathVariable Long userId,
            Authentication authentication) {

        socialService.unfollowUser(
                userId, currentUserId(authentication));

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{userId}/following")
    public ResponseEntity<List<Follow>> getFollowing(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                socialService.getFollowing(userId));
    }

    @GetMapping("/users/{userId}/followers")
    public ResponseEntity<List<Follow>> getFollowers(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                socialService.getFollowers(userId));
    }

    @GetMapping("/users/{userId}/followers/count")
    public ResponseEntity<Long> countFollowers(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                socialService.countFollowers(userId));
    }

    @GetMapping("/users/{userId}/following/count")
    public ResponseEntity<Long> countFollowing(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                socialService.countFollowing(userId));
    }
}