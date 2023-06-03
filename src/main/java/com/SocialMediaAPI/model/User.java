package com.SocialMediaAPI.model;

import com.SocialMediaAPI.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.*;

@Entity
@Table(name = "USERS")
@Setter
@Getter
public class User extends AbstractSender implements UserDetails, Sender {
    @Column(unique = true, nullable = false, length = 25)
    @NotNull
    private String username;

    @Column(nullable = false)
    @NotNull
    private String password;

    @Column(unique = true, nullable = false)
    @NotNull
    private String email;

    @NotNull
    @ManyToMany
    private Set<User> friends = new HashSet<>();

    @NotNull
    @ManyToMany
    private Set<User> subscribers = new HashSet<>();

    @NotNull
    @ManyToMany
    private Set<User> publishers = new HashSet<>();


    @NotNull
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Notification> notifications = new HashSet<>();

    @NotNull
    @ManyToMany
    @JoinTable(
            name = "chats_users",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "chat_id", referencedColumnName = "id"
            )
    )
    private Set<Chat> chats = new HashSet<>();

    @JsonIgnore
    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return new ArrayList<GrantedAuthority>();
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public Sender createDto() {
        UserDto userDto = new UserDto();
        userDto.setId(this.getId());
        userDto.setUsername(this.getUsername());
        userDto.setEmail(this.getEmail());
        return userDto;
    }
}
