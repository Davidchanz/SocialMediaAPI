package com.SocialMediaAPI.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@TestConfiguration
public class TestConfig {

    @Bean public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance() ;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        User basicUser = new User("GN", "123", List.of(new SimpleGrantedAuthority("read")));
        return new InMemoryUserDetailsManager(List.of(
                basicUser
        ));
    }
}
