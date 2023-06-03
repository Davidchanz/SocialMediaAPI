package com.SocialMediaAPI.service;

import com.SocialMediaAPI.model.Chat;
import com.SocialMediaAPI.model.ChatMessage;
import com.SocialMediaAPI.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ChatMessageService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;


    public ChatMessage createNewMessage(ChatMessage chatMessage) {
        return chatMessageRepository.save(chatMessage);
    }

    public List<ChatMessage> findByChatOrderByCreatedAsc(Chat chat) {
        return chatMessageRepository.findByChatOrderByCreatedAsc(chat);
    }
}
