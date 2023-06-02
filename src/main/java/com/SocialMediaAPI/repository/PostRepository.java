package com.SocialMediaAPI.repository;

import com.SocialMediaAPI.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
