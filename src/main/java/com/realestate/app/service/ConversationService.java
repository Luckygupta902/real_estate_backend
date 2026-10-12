package com.realestate.app.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.realestate.app.entity.Conversation;
import com.realestate.app.entity.ConversationParticipant;
import com.realestate.app.entity.Message;
import com.realestate.app.entity.Property;
import com.realestate.app.repository.ConversationParticipantRepository;
import com.realestate.app.repository.ConversationRepository;
import com.realestate.app.repository.MessageRepository;
import com.realestate.app.repository.PropertyRepository;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository participantRepository;
    private final MessageRepository messageRepository;
    private final PropertyRepository propertyRepository;

    public ConversationService(
            ConversationRepository conversationRepository,
            ConversationParticipantRepository participantRepository,
            MessageRepository messageRepository,
            PropertyRepository propertyRepository) {

        this.conversationRepository = conversationRepository;
        this.participantRepository = participantRepository;
        this.messageRepository = messageRepository;
        this.propertyRepository = propertyRepository;
    }

    // CREATE CONVERSATION
    @Transactional
    public Conversation createConversation(
            Long propertyId, Long currentUserId) {

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Property not found"));

        Long ownerId = property.getUserId();

        if (ownerId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Property has no owner");
        }

        if (ownerId.equals(currentUserId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "You cannot start a conversation with yourself");
        }
         System.out.println("Creating conversation for propertyId: " + propertyId + ", currentUserId: " + currentUserId + ", ownerId: " + ownerId);
        Conversation conversation = new Conversation();
        conversation.setPropertyId(propertyId);
        conversation.setCreatedAt(LocalDateTime.now());
        conversation.setUpdatedAt(LocalDateTime.now());

        Conversation saved = conversationRepository.save(conversation);

        addParticipant(saved.getId(), currentUserId);
        addParticipant(saved.getId(), ownerId);

        return saved;
    }

    private void addParticipant(
            Long conversationId, Long userId) {

        ConversationParticipant participant = new ConversationParticipant();

        participant.setConversationId(conversationId);
        participant.setUserId(userId);

        participantRepository.save(participant);
    }

    // LIST MY CONVERSATIONS
    public List<Conversation> getMyConversations(Long userId) {

        return participantRepository.findByUserId(userId)
                .stream()
                .map(ConversationParticipant::getConversationId)
                .distinct()
                .map(id -> conversationRepository.findById(id)
                        .orElse(null))
                .filter(c -> c != null)
                .toList();
    }

    // ADMIN: LIST ALL CONVERSATIONS
    public List<Conversation> getAllConversations() {
        return conversationRepository.findAll();
    }

    // GET ONE CONVERSATION
    public Conversation getConversation(
            Long conversationId, Long userId, boolean isAdmin) {
 
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Conversation not found"));

        if (!isAdmin && !isParticipant(conversationId, userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not a participant in this conversation");
        }

        return conversation;
    }

    // SEND MESSAGE
    @Transactional
    public Message    sendMessage(
            Long conversationId,
            Long senderId,
            String messageText) {

        if (messageText == null || messageText.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Message cannot be empty");
        }

        if (messageText.length() > 5000) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Message cannot exceed 5000 characters");
        }

        if (!conversationRepository.existsById(conversationId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Conversation not found");
        }

        if (!isParticipant(conversationId, senderId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only conversation participants can send messages");
        }

        Message message = new Message();
        message.setConversationId(conversationId);
        message.setSenderId(senderId);
        message.setMessageText(messageText);
        message.setIsRead(false);
        message.setCreatedAt(LocalDateTime.now());

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow();

        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);

        return messageRepository.save(message);
    }

    // GET MESSAGES
    public List<Message> getMessages(
            Long conversationId, Long userId, boolean isAdmin) {

        getConversation(conversationId, userId, isAdmin);

        return messageRepository
                .findByConversationIdOrderByCreatedAtAsc(
                        conversationId);
    }

    // MARK A MESSAGE AS READ
    @Transactional
    public Message markMessageAsRead(
            Long conversationId,
            Long messageId,
            Long userId) {

        if (!isParticipant(conversationId, userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not a participant");
        }

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Message not found"));

        if (!conversationId.equals(message.getConversationId())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Message does not belong to this conversation");
        }

        // A sender should not mark their own message as read.
        if (userId.equals(message.getSenderId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "You cannot mark your own message as read");
        }

        message.setIsRead(true);
        return messageRepository.save(message);
    }

    // DELETE CONVERSATION (ADMIN ONLY AT CONTROLLER LEVEL)
    @Transactional
    public void deleteConversation(Long conversationId) {

        if (!conversationRepository.existsById(conversationId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Conversation not found");
        }

        messageRepository.deleteByConversationId(conversationId);
        participantRepository.deleteByConversationId(conversationId);
        conversationRepository.deleteById(conversationId);
    }

    private boolean isParticipant(
            Long conversationId, Long userId) {

        return participantRepository
                .existsByConversationIdAndUserId(
                        conversationId, userId);
    }
}
