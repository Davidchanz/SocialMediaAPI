package com.SocialMediaAPI.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ApiResponse {

    public ApiResponse(String response) {
        this.response = response;
    }

    @NotNull
    @Size(max = 1000)
    private String response;

    @NotNull
    private Instant created = Instant.now();
}
