package com.SocialMediaAPI.controller;

import com.SocialMediaAPI.service.TokenService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final TokenService tokenService;

    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/token")
    public String token(Authentication authentication){
        System.out.println("Token for user " + authentication.getName());
        String token = tokenService.generateToken(authentication);
        System.out.println("Token Granted " + token);
        return token;
    }
}
