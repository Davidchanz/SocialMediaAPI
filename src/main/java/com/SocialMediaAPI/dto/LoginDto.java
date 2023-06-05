package com.SocialMediaAPI.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginDto {
    @NotNull
    private String username;

    @NotNull
    private String password;

    @Override
    public String toString() {
        return "{" +
                "username: '" + username + '\'' +
                ", password: '" + password + '\'' +
                '}';
    }
}
