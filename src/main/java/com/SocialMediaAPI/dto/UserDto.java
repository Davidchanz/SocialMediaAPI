package com.SocialMediaAPI.dto;

import com.SocialMediaAPI.model.Chat;
import com.SocialMediaAPI.model.ChatMessage;
import com.SocialMediaAPI.model.Sender;
import com.SocialMediaAPI.model.User;
import com.SocialMediaAPI.service.ChatMessageService;
import com.SocialMediaAPI.validation.annotation.ValidEmail;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class UserDto implements Sender {
    @NotNull
    private Long id;

    @NotNull
    private String username;

    @NotNull
    private String email;

    public static Sender createUserDto(Sender sender){
        return sender.createDto();
    }

    @Override
    public Sender createDto() {
        return this;
    }
}
