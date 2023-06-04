package com.SocialMediaAPI.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ApiErrorDto {

    @NotNull
    private String title;

    @NotNull
    private int status;

    @NotNull
    private String instance;

    @NotNull
    private Instant created;

    @NotNull
    private List<String> errors = new ArrayList<>();

    public ApiErrorDto(HttpStatus httpStatus, String instance, String... errors){
        this.title = httpStatus.name();
        this.status = httpStatus.value();
        this.instance = instance;
        this.errors = List.of(errors);
    }
}
