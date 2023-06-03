package com.SocialMediaAPI.dto;

import com.SocialMediaAPI.model.ChatMessage;
import com.SocialMediaAPI.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Setter
@Getter
@Builder
public class ChatMessagesDto {
    @NotNull
    private Long id;

    @NotNull
    private String message;

    @NotNull
    private Instant created;

    @NotNull
    private UserDto author;

    public static ChatMessagesDto createChatMessageDto(ChatMessage message){
        return ChatMessagesDto
                .builder()
                .message(message.getMessage())
                .author((UserDto) UserDto.createUserDto(message.getAuthor()))
                .created(message.getCreated())
                .id(message.getId())
                .build();
    }
}
