package com.SocialMediaAPI.repository;

import com.SocialMediaAPI.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

}
