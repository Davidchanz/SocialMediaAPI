package com.SocialMediaAPI.service;

import com.SocialMediaAPI.exception.ChatNotFoundException;
import com.SocialMediaAPI.model.Chat;
import com.SocialMediaAPI.model.ChatMessage;
import com.SocialMediaAPI.model.ChatType;
import com.SocialMediaAPI.model.User;
import com.SocialMediaAPI.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    public Chat createNewChat(Chat chat){
        return chatRepository.save(chat);
    }

    public void deleteChat(User user, User friend) {
        for (Chat chat : user.getChats()) {
            if(chat.getUsers().contains(friend) && chat.getType() == ChatType.PRIVATE) {
                chat.removeUser(user);
                chat.removeUser(friend);
                chatRepository.delete(chat);
                break;
            }
        }
    }

    public Chat findChatById(long id) {
        return chatRepository.findById(id).orElseThrow(() -> new ChatNotFoundException("Chat with id: " + id + " not found!"));
    }

    public void addMessageToChat(Chat chat, ChatMessage chatMessage) {
        chat.getMessages().add(chatMessage);
        chatRepository.save(chat);
    }
}
