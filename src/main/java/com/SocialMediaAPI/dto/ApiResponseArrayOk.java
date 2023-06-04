package com.SocialMediaAPI.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class ApiResponseArrayOk<T> extends ApiResponse{
    @NotNull
    private List<T> responses = new ArrayList<>();

    public ApiResponseArrayOk(String title, T... responses) {
        this.status = 200;
        this.title = title;
        this.responses = List.of(responses);
    }
}
