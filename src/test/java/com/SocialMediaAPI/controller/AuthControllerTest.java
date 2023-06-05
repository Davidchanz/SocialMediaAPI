package com.SocialMediaAPI.controller;

import com.SocialMediaAPI.config.TestConfig;
import com.SocialMediaAPI.configuration.SecurityConfig;
import com.SocialMediaAPI.dto.LoginDto;
import com.SocialMediaAPI.dto.UserAuthDto;
import com.SocialMediaAPI.model.CustomUserDetails;
import com.SocialMediaAPI.model.User;
import com.SocialMediaAPI.security.JwtAuthenticationEntryPoint;
import com.SocialMediaAPI.security.JwtTokenProvider;
import com.SocialMediaAPI.service.AuthService;
import com.SocialMediaAPI.service.CustomUserDetailsService;
import com.SocialMediaAPI.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.Charset;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ AuthController.class })
@Import({ SecurityConfig.class, TestConfig.class })
public class AuthControllerTest extends AbstractTest{

    @MockBean
    AuthService authService;

    @MockBean
    UserService userService;

    @MockBean
    Authentication authentication;

    @InjectMocks
    AuthController authController;

    @BeforeEach
    public void setUp() {
        super.setUp(authController);
    }

    @Test
    void login() throws Exception {
        when(authService.authenticateUser(any(LoginDto.class))).thenReturn(Optional.ofNullable(authentication));
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(authService.generateToken(any(CustomUserDetails.class))).thenReturn(jwtTokenProvider.generateToken(customUserDetails));

        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("admin");
        loginDto.setPassword("password");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson=ow.writeValueAsString(loginDto);

        MvcResult result = this.mvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON_UTF8).content(requestJson))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void register() throws Exception {
        UserAuthDto userAuthDto = new UserAuthDto();
        userAuthDto.setEmail("admin@email.com");
        userAuthDto.setUsername("admin");
        userAuthDto.setMatchingPassword("password");
        userAuthDto.setPassword("password");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(userAuthDto);

        MvcResult result = this.mvc.perform(post("/api/auth/registration")
                        .contentType(APPLICATION_JSON_UTF8).content(requestJson))
                .andExpect(status().isOk())
                .andReturn();
    }
}
