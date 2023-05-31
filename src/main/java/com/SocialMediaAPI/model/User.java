package com.SocialMediaAPI.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.Operation;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "USERS")
@Setter
@Getter
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    @NotNull
    private Long id;

    @Column(unique = true, nullable = false, length = 25)
    @Size(min = 5, max = 25)
    @NotBlank
    private String username;

    @Column(nullable = false, length = 25)
    @Size(min = 8, max = 25)
    @NotBlank
    private String password;

   /* @Column(length = 25)
    @Size(min = 5, max = 25)
    @NotBlank
    private String firstName;

    @Column(length = 25)
    @Size(min = 5, max = 25)
    @NotBlank
    private String lastName;

    @Column(unique = true, nullable = false)
    @Size(min = 8, max = 255)
    @NotBlank
    private String email;*/

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
}
