package com.SocialMediaAPI.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("/login")
    public String showLoginPage(){
        return "login";
    }
}
