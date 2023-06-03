package com.SocialMediaAPI.dto;

import com.SocialMediaAPI.model.AbstractSender;
import com.SocialMediaAPI.model.NotificationType;
import com.SocialMediaAPI.model.Sender;
import com.SocialMediaAPI.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NotificationDto {
    @NotNull
    private Long id;

    @NotNull
    private String message;

    @NotNull
    private Sender from;

    @NotNull
    private UserDto to;

    @NotNull
    private NotificationType type;
}
