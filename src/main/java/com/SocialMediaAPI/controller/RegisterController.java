package com.SocialMediaAPI.controller;

import com.SocialMediaAPI.dto.UserDto;
import com.SocialMediaAPI.exception.UserAlreadyExistException;
import com.SocialMediaAPI.model.User;
import com.SocialMediaAPI.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegisterController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public String register(@Valid @RequestBody UserDto userDto){
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setEmail(userDto.getEmail());
        userService.registerNewUserAccount(user);
        return "User '" + user.getUsername() + "' successfully created!";
    }

}
