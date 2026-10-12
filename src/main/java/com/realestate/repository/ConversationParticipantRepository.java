package com.realestate.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.entity.ConversationParticipant;

public interface ConversationParticipantRepository extends JpaRepository<ConversationParticipant, Long> {

}
