package com.realestate.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.realestate.app.entity.Conversation;
@Repository 
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

}
