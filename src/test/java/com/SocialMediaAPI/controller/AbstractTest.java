package com.SocialMediaAPI.controller;

import com.SocialMediaAPI.config.TestConfig;
import com.SocialMediaAPI.configuration.SecurityConfig;
import com.SocialMediaAPI.model.*;
import com.SocialMediaAPI.security.JwtAuthenticationEntryPoint;
import com.SocialMediaAPI.security.JwtTokenProvider;
import com.SocialMediaAPI.service.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.Charset;
import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Set;

@Import({ SecurityConfig.class, TestConfig.class })
public class AbstractTest {

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    @Autowired
    MockMvc mvc;

    @MockBean
    CustomUserDetailsService customUserDetailsService;

    @MockBean
    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    String token;

    HttpHeaders headers;

    @Mock
    Principal principal;

    Post post;

    Chat chat;

    User user;

    User friend;

    ChatMessage chatMessage;

    Notification notification;

    CustomUserDetails customUserDetails = getCustomUserDetails();

    {
        user = getUser();
        friend = getFriend();
        chat = getChat();
        user.setChats(Set.of(chat));
        friend.setChats(Set.of(chat));
        post = getPost();
        chatMessage = getChatMessage();
        notification = getNotification();
    }

    public void setUp(Object controller){
        MockitoAnnotations.openMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        token = jwtTokenProvider.generateToken(customUserDetails);
        headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
    }

    public CustomUserDetails getCustomUserDetails(){
        CustomUserDetails customUserDetails = new CustomUserDetails();
        customUserDetails.setEmail("admin@email.com");
        customUserDetails.setPassword("password");
        customUserDetails.setUsername("admin");
        customUserDetails.setId(1L);
        return customUserDetails;
    }

    public Post getPost(){
        Post post = new Post();
        post.setId(1L);
        post.setImages(new ArrayList<>());
        post.setText("text");
        post.setHeader("header");
        post.setCreated(Instant.now());
        post.setUser(user);
        return post;
    }

    public User getUser(){
        com.SocialMediaAPI.model.User user = new User();
        user.setId(1L);
        user.setPassword("password");
        user.setEmail("email");
        user.setUsername("testuser");
        user.setChats(Set.of());
        return user;
    }

    public User getFriend(){
        com.SocialMediaAPI.model.User user = new User();
        user.setId(2L);
        user.setPassword("password");
        user.setEmail("email");
        user.setUsername("testuserfriend");
        user.setChats(Set.of());
        return user;
    }

    public Chat getChat(){
        Chat chat = new Chat();
        chat.setType(ChatType.PRIVATE);
        chat.setName("chat_name");
        chat.setId(1L);
        chat.setUsers(Set.of(user));
        chat.setMessages(Set.of());
        return chat;
    }

    public ChatMessage getChatMessage(){
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChat(chat);
        chatMessage.setAuthor(user);
        chatMessage.setCreated(Instant.now());
        chatMessage.setId(1L);
        return chatMessage;
    }

    public Notification getNotification(){
        Notification notification = new Notification();
        notification.setMessage("You have a new message in " + chat.getName());
        notification.setFrom(chat);
        notification.setTo(user);
        notification.setType(NotificationType.CHAT_MESSAGE);
        return notification;
    }
}
