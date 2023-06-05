package com.SocialMediaAPI.controller;

import com.SocialMediaAPI.dto.ApiErrorDto;
import com.SocialMediaAPI.dto.LoginDto;
import com.SocialMediaAPI.dto.TokenDto;
import com.SocialMediaAPI.exception.UserLoginException;
import com.SocialMediaAPI.model.CustomUserDetails;
import com.SocialMediaAPI.model.User;
import com.SocialMediaAPI.security.JwtTokenProvider;
import com.SocialMediaAPI.service.AuthService;
import com.SocialMediaAPI.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthService authService;

    @Operation(summary = "Get user token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get user token",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TokenDto.class)) })})

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginRequest){
        Authentication authentication = authService.authenticateUser(loginRequest)
                .orElseThrow(() -> new UserLoginException("Couldn't login user [" + loginRequest + "]"));

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        System.out.println("Logged in User returned [API]: " + customUserDetails.getUsername());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwtToken = authService.generateToken(customUserDetails);
        TokenDto token = new TokenDto();
        token.setToken(jwtToken);
        return new ResponseEntity<>(token, HttpStatus.OK);

        /*return authService.createAndPersistRefreshTokenForDevice(authentication, loginRequest)
                //.map(RefreshToken::getToken)
                .map(refreshToken -> {
                    String jwtToken = authService.generateToken(customUserDetails);
                    TokenDto token = new TokenDto();
                    token.setToken(jwtToken);
                    return new ResponseEntity<>(token, HttpStatus.OK);
                    //return ResponseEntity.ok(new JwtAuthenticationResponse(jwtToken, refreshToken, tokenProvider.getExpiryDuration()));
                })
                .orElseThrow(() -> new UserLoginException("Couldn't create refresh token for: [" + loginRequest + "]"));
    */}
}
