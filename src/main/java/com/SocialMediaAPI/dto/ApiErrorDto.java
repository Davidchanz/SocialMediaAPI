package com.SocialMediaAPI.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class ApiErrorDto {
    @NotNull
    private List<String> errors;
}
