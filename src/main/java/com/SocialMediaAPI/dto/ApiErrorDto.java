package com.SocialMediaAPI.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ApiErrorDto {

    @NotNull
    private List<String> errors = new ArrayList<>();

    public ApiErrorDto(String... errors){
        this .errors = List.of(errors);
    }
}
