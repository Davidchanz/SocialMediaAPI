package com.SocialMediaAPI.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public abstract class ApiResponse {
    @NotNull
    protected Instant created = Instant.now();

    @NotNull
    protected int status;

    @NotNull
    protected String title;
}
