package com.SocialMediaAPI.dto;

import com.SocialMediaAPI.model.ChatMessage;
import com.SocialMediaAPI.model.Sender;
import com.SocialMediaAPI.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Builder
public class ChatDto implements Sender {
    @NotNull
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private Set<UserDto> users = new HashSet<>();;

    @Override
    public Sender createDto() {
        return this;
    }
}
