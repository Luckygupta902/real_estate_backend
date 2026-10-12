package com.realestate.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.realestate.app.entity.ConversationParticipant;
@Repository 
public interface ConversationParticipantRepository extends JpaRepository<ConversationParticipant, Long> {
    List<ConversationParticipant> findByUserId(Long userId);

    List<ConversationParticipant> findByConversationId(
            Long conversationId);

    boolean existsByConversationIdAndUserId(
            Long conversationId, Long userId);

    void deleteByConversationId(Long conversationId);
}
