package com.SocialMediaAPI.controller;

import com.SocialMediaAPI.dto.ApiErrorDto;
import com.SocialMediaAPI.dto.TokenDto;
import com.SocialMediaAPI.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final TokenService tokenService;

    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Operation(summary = "Get user token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get user token",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TokenDto.class)) })})
    @PostMapping("/token")
    public TokenDto token(Authentication authentication){
        //System.out.println("Token for user " + authentication.getName());
        TokenDto token = new TokenDto();
        token.setToken(tokenService.generateToken(authentication));
        //System.out.println("Token Granted " + token);
        return token;
    }
}
