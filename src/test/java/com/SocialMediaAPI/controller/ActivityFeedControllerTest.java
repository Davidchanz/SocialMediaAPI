package com.SocialMediaAPI.controller;

import com.SocialMediaAPI.config.TestConfig;
import com.SocialMediaAPI.configuration.SecurityConfig;
import com.SocialMediaAPI.model.User;
import com.SocialMediaAPI.service.ActivityFeedService;
import com.SocialMediaAPI.service.UserService;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.List;

@WebMvcTest({ ActivityFeedController.class })
@Import({ SecurityConfig.class, TestConfig.class })
class ActivityFeedControllerTest extends AbstractTest{

    @InjectMocks
    ActivityFeedController activityFeedController;

    @MockBean
    ActivityFeedService activityFeedService;

    @MockBean
    UserService userService;

    @BeforeEach
    public void setUp() {
        super.setUp(activityFeedController);
    }

    @Test
    void getActivityFeedPageable() throws Exception {
        when(userService.findUserByUserName(any(String.class))).thenReturn(user);
        when(principal.getName()).thenReturn(user.getUsername());
        when(activityFeedService.getMaxPageNumber(any(User.class), any(Integer.class))).thenReturn(1);
        when(activityFeedService
                .findAllActivityFeedPostsOrderByCreatedDescPageable(
                        any(User.class), any(Pageable.class))).thenReturn(List.of(post));

        ReflectionTestUtils.setField(activityFeedController, "pageSize", 3);

        this.mvc.perform(MockMvcRequestBuilders.get("/feed")
                        .param("page", "1")
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    void getActivityFeedAll() throws Exception {
        when(userService.findUserByUserName(any(String.class))).thenReturn(user);
        when(principal.getName()).thenReturn(user.getUsername());
        when(activityFeedService
                .findAllActivityFeedPostsOrderByCreatedDesc(
                        any(User.class))).thenReturn(List.of(post));

        this.mvc.perform(MockMvcRequestBuilders.get("/feed/all")
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isOk());
    }
}