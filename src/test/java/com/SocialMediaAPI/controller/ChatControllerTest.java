package com.SocialMediaAPI.controller;

import com.SocialMediaAPI.config.TestConfig;
import com.SocialMediaAPI.configuration.SecurityConfig;
import com.SocialMediaAPI.model.Chat;
import com.SocialMediaAPI.model.ChatMessage;
import com.SocialMediaAPI.model.Notification;
import com.SocialMediaAPI.service.ChatMessageService;
import com.SocialMediaAPI.service.ChatService;
import com.SocialMediaAPI.service.NotificationService;
import com.SocialMediaAPI.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ ChatController.class })
@Import({ SecurityConfig.class, TestConfig.class })
class ChatControllerTest extends AbstractTest{
    @InjectMocks
    ChatController chatController;

    @MockBean
    private ChatService chatService;

    @MockBean
    private UserService userService;

    @MockBean
    private ChatMessageService chatMessageService;

    @MockBean
    private NotificationService notificationService;

    @BeforeEach
    public void setUp() {
        super.setUp(chatController);
    }

    @Test
    void getAllChats() throws Exception {
        when(principal.getName()).thenReturn(user.getUsername());
        when(userService.findUserByUserName(any(String.class))).thenReturn(user);

        this.mvc.perform(get("/chats")
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    void getChatMessageHistory() throws Exception {
        when(principal.getName()).thenReturn(user.getUsername());
        when(userService.findUserByUserName(any(String.class))).thenReturn(user);
        when(chatService.findChatById(any(Long.class))).thenReturn(chat);
        when(chatMessageService.findByChatOrderByCreatedAsc(any(Chat.class))).thenReturn(new ArrayList<>());

        this.mvc.perform(get("/chat/history")
                        .param("chatId", "1")
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    void sendMessage() throws Exception {
        when(principal.getName()).thenReturn(user.getUsername());
        when(userService.findUserByUserName(any(String.class))).thenReturn(user);
        when(chatService.findChatById(any(Long.class))).thenReturn(chat);
        when(chatMessageService.createNewMessage(any(ChatMessage.class))).thenReturn(chatMessage);
        when(notificationService.createNotification(any(Notification.class))).thenReturn(notification);

       this.mvc.perform(MockMvcRequestBuilders.post("/chat/send")
                        .param("chatId", "1")
                        .param("message", "chat message text!")
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isOk());
    }
}