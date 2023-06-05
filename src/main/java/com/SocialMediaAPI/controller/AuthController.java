package com.SocialMediaAPI.controller;

import com.SocialMediaAPI.dto.ApiErrorDto;
import com.SocialMediaAPI.dto.LoginDto;
import com.SocialMediaAPI.dto.TokenDto;
import com.SocialMediaAPI.dto.UserAuthDto;
import com.SocialMediaAPI.exception.UserLoginException;
import com.SocialMediaAPI.model.CustomUserDetails;
import com.SocialMediaAPI.model.User;
import com.SocialMediaAPI.security.JwtTokenProvider;
import com.SocialMediaAPI.service.AuthService;
import com.SocialMediaAPI.service.TokenService;
import com.SocialMediaAPI.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Operation(summary = "Register new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registered new user",
                    content = { @Content(mediaType = "text/plain",
                            examples = @ExampleObject("User 'username' successfully created!")) }),
            @ApiResponse(responseCode = "400", description = "Invalid user input",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDto.class)) }),
            @ApiResponse(responseCode = "409", description = "User already registered",
                    content = { @Content(mediaType = "text/plain",
                            examples = @ExampleObject("User with username: 'username' is already exist!")) }) })

    @PostMapping("/registration")
    public String register(@Valid @RequestBody UserAuthDto userAuthDto){
        User user = new User();
        user.setUsername(userAuthDto.getUsername());
        user.setPassword(userAuthDto.getPassword());
        user.setEmail(userAuthDto.getEmail());
        userService.registerNewUserAccount(user);
        return "User '" + user.getUsername() + "' successfully created!";
    }

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
    }
}
