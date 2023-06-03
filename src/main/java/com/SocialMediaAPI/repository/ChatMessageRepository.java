package com.SocialMediaAPI.repository;

import com.SocialMediaAPI.model.Chat;
import com.SocialMediaAPI.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatOrderByCreatedAsc(Chat chat);
}
