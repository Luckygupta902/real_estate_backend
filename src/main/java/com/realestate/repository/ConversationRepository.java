package com.realestate.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.entity.Conversation;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

}
