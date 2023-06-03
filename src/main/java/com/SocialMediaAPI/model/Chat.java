package com.SocialMediaAPI.model;

import com.SocialMediaAPI.dto.ChatDto;
import com.SocialMediaAPI.dto.UserDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table
@Setter
@Getter
public class Chat extends AbstractSender implements Sender{

    @NotNull
    @Column
    private String name;

    @NotNull
    @ManyToMany(mappedBy = "chats")
    private Set<User> users = new HashSet<>();

    @NotNull
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "chat")
    private Set<ChatMessage> messages = new HashSet<>();

    @NotNull
    @Enumerated(EnumType.STRING)
    private ChatType type;

    public void removeUser(User user) {
        this.users.remove(user);
        user.getChats().remove(this);
    }

    @Override
    public Sender createDto() {
        return ChatDto.builder()
                .id(this.getId())
                .name(this.getName())
                .users(this.getUsers().stream()
                        .map(UserDto::createUserDto)
                        .map(sender1 -> (UserDto) sender1)
                        .collect(Collectors.toSet())
                )
                .build();
    }
}
