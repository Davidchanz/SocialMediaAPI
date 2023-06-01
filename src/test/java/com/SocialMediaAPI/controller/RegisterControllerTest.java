package com.SocialMediaAPI.controller;

import com.SocialMediaAPI.config.TestConfig;
import com.SocialMediaAPI.configuration.SecurityConfig;
import com.SocialMediaAPI.model.User;
import com.SocialMediaAPI.service.TokenService;
import com.SocialMediaAPI.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ RegisterController.class })
@Import({ SecurityConfig.class, TokenService.class, TestConfig.class })
class RegisterControllerTest {

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);

    @Autowired
    MockMvc mvc;

    @MockBean
    UserService userService;

    @Test
    void registerStatusIsOK() throws Exception {
        var requestJson = """
                {
                   "username": "admin",
                    "password": "password",
                    "matchingPassword": "password",
                    "email": "admin@mail.com"
                }
                """;

        this.mvc.perform(post("/register")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string("User 'admin' successfully created!"));
    }

    @Test
    void registerValidationErrorUserName() throws Exception {
        var requestJson = """
                {
                   "username": null,
                    "password": "pass",
                    "matchingPassword": "1",
                    "email": "admin@mail.c"
                }
                """;

        this.mvc.perform(post("/register")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("""
                        {
                            "errors": [
                                "Password must be between 8 and 25!",
                                "Invalid email",
                                "Username must not be null!",
                                "Passwords don't match"
                            ]
                        }
                        """));
    }
}