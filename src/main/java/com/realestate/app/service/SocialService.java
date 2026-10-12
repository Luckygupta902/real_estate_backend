package com.realestate.app.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.realestate.app.entity.Like;
import com.realestate.app.entity.Comment;
import com.realestate.app.entity.Follow;
import com.realestate.app.repository.LikeRepository;
import com.realestate.app.repository.CommentRepository;
import com.realestate.app.repository.FollowRepository;
import com.realestate.app.repository.PropertyRepository;
import com.realestate.app.repository.UserRepository;

@Service
public class SocialService {

    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final FollowRepository followRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    public SocialService(
            LikeRepository likeRepository,
            CommentRepository commentRepository,
            FollowRepository followRepository,
            PropertyRepository propertyRepository,
            UserRepository userRepository) {

        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.followRepository = followRepository;
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
    }

    private void checkProperty(Long propertyId) {
        if (propertyId == null ||
                !propertyRepository.existsById(propertyId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Property not found");
        }
    }

    private void checkUser(Long userId) {
        if (userId == null || !userRepository.existsById(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User not found");
        }
    }

    // ---------------- LIKES ----------------

    @Transactional
    public Like likeProperty(Long propertyId, Long userId) {
        checkProperty(propertyId);

        if (likeRepository.existsByUserIdAndPropertyId(
                userId, propertyId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "You have already liked this property");
        }

        Like like = new Like();
        like.setUserId(userId);
        like.setPropertyId(propertyId);
        like.setCreatedAt(LocalDateTime.now());

        return likeRepository.save(like);
    }

    @Transactional
    public void unlikeProperty(Long propertyId, Long userId) {
        checkProperty(propertyId);

        if (!likeRepository.existsByUserIdAndPropertyId(
                userId, propertyId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "You have not liked this property");
        }

        likeRepository.deleteByUserIdAndPropertyId(userId, propertyId);
    }

    public List<Like> getMyLikes(Long userId) {
        return likeRepository.findByUserId(userId);
    }

    public List<Like> getPropertyLikes(Long propertyId) {
        checkProperty(propertyId);
        return likeRepository.findByPropertyId(propertyId);
    }

    public long countPropertyLikes(Long propertyId) {
        checkProperty(propertyId);
        return likeRepository.countByPropertyId(propertyId);
    }

    // ---------------- COMMENTS ----------------

    @Transactional
    public Comment addComment(
            Long propertyId, Long userId, String commentText) {

        checkProperty(propertyId);

        if (commentText == null || commentText.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Comment text is required");
        }

        commentText = commentText.trim();

        if (commentText.length() > 2000) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Comment cannot exceed 2000 characters");
        }

        Comment comment = new Comment();
        comment.setPropertyId(propertyId);
        comment.setUserId(userId);
        comment.setCommentText(commentText);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    public List<Comment> getPropertyComments(Long propertyId) {
        checkProperty(propertyId);
        return commentRepository
                .findByPropertyIdOrderByCreatedAtDesc(propertyId);
    }

    public List<Comment> getMyComments(Long userId) {
        return commentRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public Comment updateComment(
            Long commentId, Long userId,
            boolean isAdmin, String commentText) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Comment not found"));

        if (!isAdmin && !comment.getUserId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only edit your own comments");
        }

        if (commentText == null || commentText.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Comment text is required");
        }

        commentText = commentText.trim();

        if (commentText.length() > 2000) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Comment cannot exceed 2000 characters");
        }

        comment.setCommentText(commentText);
        comment.setUpdatedAt(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(
            Long commentId, Long userId, boolean isAdmin) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Comment not found"));

        if (!isAdmin && !comment.getUserId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only delete your own comments");
        }

        commentRepository.delete(comment);
    }

    // ---------------- FOLLOWS ----------------

    @Transactional
    public Follow followUser(Long followingId, Long followerId) {
        checkUser(followerId);
        checkUser(followingId);

        if (followerId.equals(followingId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "You cannot follow yourself");
        }

        if (followRepository.existsByFollowerIdAndFollowingId(
                followerId, followingId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "You are already following this user");
        }

        Follow follow = new Follow();
        follow.setFollowerId(followerId);
        follow.setFollowingId(followingId);
        follow.setCreatedAt(LocalDateTime.now());

        return followRepository.save(follow);
    }

    @Transactional
    public void unfollowUser(Long followingId, Long followerId) {
        if (!followRepository.existsByFollowerIdAndFollowingId(
                followerId, followingId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "You are not following this user");
        }

        followRepository.deleteByFollowerIdAndFollowingId(
                followerId, followingId);
    }

    public List<Follow> getFollowing(Long userId) {
        checkUser(userId);
        return followRepository.findByFollowerId(userId);
    }

    public List<Follow> getFollowers(Long userId) {
        checkUser(userId);
        return followRepository.findByFollowingId(userId);
    }

    public long countFollowers(Long userId) {
        checkUser(userId);
        return followRepository.countByFollowingId(userId);
    }

    public long countFollowing(Long userId) {
        checkUser(userId);
        return followRepository.countByFollowerId(userId);
    }
}