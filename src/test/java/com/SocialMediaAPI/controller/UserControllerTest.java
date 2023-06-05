package com.SocialMediaAPI.controller;

import com.SocialMediaAPI.config.TestConfig;
import com.SocialMediaAPI.configuration.SecurityConfig;
import com.SocialMediaAPI.model.Chat;
import com.SocialMediaAPI.model.Notification;
import com.SocialMediaAPI.model.User;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.floatThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ UserController.class })
@Import({ SecurityConfig.class, TestConfig.class })
class UserControllerTest extends AbstractTest{
    @InjectMocks
    UserController userController;

    @MockBean
    private UserService userService;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private ChatService chatService;

    @BeforeEach
    public void setUp() {
        super.setUp(userController);
    }

    @Test
    void sendFriendInvite() throws Exception {
        when(principal.getName()).thenReturn(user.getUsername());
        when(userService.findUserByUserName(user.getUsername())).thenReturn(user);
        when(userService.findUserByUserName(friend.getUsername())).thenReturn(friend);
        when(notificationService.isInviteExist(any(User.class), any(User.class))).thenReturn(false);
        when(notificationService.createNotification(any(Notification.class))).thenReturn(notification);

        this.mvc.perform(MockMvcRequestBuilders.post("/friend/invite")
                        .param("username", "testuserfriend")
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    void rejectFriendInvite() throws Exception {
        when(principal.getName()).thenReturn(user.getUsername());
        when(userService.findUserByUserName(user.getUsername())).thenReturn(user);
        when(userService.findUserByUserName(friend.getUsername())).thenReturn(friend);
        when(notificationService.findNotificationById(any(Long.class))).thenReturn(notification);

        this.mvc.perform(MockMvcRequestBuilders.post("/friend/invite/reject")
                        .param("inviteId", "1")
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    void acceptFriendInvite() throws Exception {
        when(principal.getName()).thenReturn(user.getUsername());
        when(userService.findUserByUserName(user.getUsername())).thenReturn(user);
        when(userService.findUserByUserName(friend.getUsername())).thenReturn(friend);
        when(notificationService.findNotificationById(any(Long.class))).thenReturn(notification);
        when(chatService.createNewChat(any(Chat.class))).thenReturn(chat);


        this.mvc.perform(MockMvcRequestBuilders.post("/friend/invite/reject")
                        .param("inviteId", "1")
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    void subscribe() throws Exception {
        when(principal.getName()).thenReturn(user.getUsername());
        when(userService.findUserByUserName(user.getUsername())).thenReturn(user);
        when(userService.findUserByUserName(friend.getUsername())).thenReturn(friend);

        this.mvc.perform(MockMvcRequestBuilders.post("/subscribe")
                        .param("username", "testuserfriend")
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    void unSubscribe() throws Exception {
        when(principal.getName()).thenReturn(user.getUsername());
        when(userService.findUserByUserName(user.getUsername())).thenReturn(user);
        when(userService.findUserByUserName(friend.getUsername())).thenReturn(friend);
        user.getPublishers().add(friend);

        this.mvc.perform(MockMvcRequestBuilders.post("/unSubscribe")
                        .param("username", "testuserfriend")
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    void removeFriend() throws Exception {
        when(principal.getName()).thenReturn(user.getUsername());
        when(userService.findUserByUserName(user.getUsername())).thenReturn(user);
        when(userService.findUserByUserName(friend.getUsername())).thenReturn(friend);
        user.getFriends().add(friend);

        this.mvc.perform(MockMvcRequestBuilders.post("/friend/remove")
                        .param("username", "testuserfriend")
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    void getNotifications() throws Exception {
        when(principal.getName()).thenReturn(user.getUsername());
        when(userService.findUserByUserName(user.getUsername())).thenReturn(user);
        when(userService.findUserByUserName(friend.getUsername())).thenReturn(friend);
        user.getFriends().add(friend);

        this.mvc.perform(get("/notifications")
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isOk());
    }
}