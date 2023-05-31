package com.SocialMediaAPI.controller;

import com.SocialMediaAPI.configuration.security.UserAuthenticationService;
import com.SocialMediaAPI.model.User;
import com.SocialMediaAPI.service.UserService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/public")
final class LoginController {
    @Autowired
    private UserAuthenticationService authentication;

    private UserService users;

    @PostMapping("/register")
    public String register(@RequestParam("username") final String username, @RequestParam("password") final String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        users.saveUser(user);

        return authentication.login(username, password)
                .orElseThrow(() -> new RuntimeException("invalid login and/or password"));
    }

    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> body) {
        System.out.println("/login: " + body);
        return authentication
                .login(body.get("username"), body.get("password"))
                .orElseThrow(() -> new RuntimeException("invalid login and/or password"));
    }
}
