package com.SocialMediaAPI.controller;

import com.SocialMediaAPI.dto.ApiErrorDto;
import com.SocialMediaAPI.dto.UserAuthDto;
import com.SocialMediaAPI.model.User;
import com.SocialMediaAPI.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegisterController {

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
    @PostMapping("/register")
    public String register(@Valid @RequestBody UserAuthDto userAuthDto){
        User user = new User();
        user.setUsername(userAuthDto.getUsername());
        user.setPassword(userAuthDto.getPassword());
        user.setEmail(userAuthDto.getEmail());
        userService.registerNewUserAccount(user);
        return "User '" + user.getUsername() + "' successfully created!";
    }

}
