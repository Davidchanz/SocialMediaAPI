package com.SocialMediaAPI.security;

import com.SocialMediaAPI.config.TestConfig;
import com.SocialMediaAPI.configuration.SecurityConfig;
import com.SocialMediaAPI.model.CustomUserDetails;
import com.SocialMediaAPI.model.User;
import com.SocialMediaAPI.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class JwtTokenProviderTest {

    private static final long jwtExpiryInMs = 25000;

    private JwtTokenProvider tokenProvider;

    @Autowired
    public JwtEncoder encoder;

    @Autowired
    public JwtDecoder decoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        this.tokenProvider = new JwtTokenProvider(jwtExpiryInMs);
        this.tokenProvider.encoder = encoder;
        this.tokenProvider.decoder = decoder;
    }

    @Test
    void testGetUserIdFromJWT() {
        String token = tokenProvider.generateToken(stubCustomUser());
        assertEquals(100, tokenProvider.getUserIdFromJWT(token).longValue());
    }

    @Test
    void testGetAuthoritiesFromJWT() {
        String token = tokenProvider.generateToken(stubCustomUser());
        assertNotNull(tokenProvider.getAuthoritiesFromJWT(token));
    }

    private CustomUserDetails stubCustomUser() {
        User user = new User();
        user.setId((long) 100);
        return new CustomUserDetails(user);
    }
}
