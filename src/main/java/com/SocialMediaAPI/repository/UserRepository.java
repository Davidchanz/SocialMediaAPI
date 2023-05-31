package com.SocialMediaAPI.repository;

import com.SocialMediaAPI.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    public Optional<User> findByUsername(String username);

    /*public Optional<User> findByEmail(String email);*/
}
