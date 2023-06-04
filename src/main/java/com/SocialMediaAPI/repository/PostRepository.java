package com.SocialMediaAPI.repository;

import com.SocialMediaAPI.model.Post;
import com.SocialMediaAPI.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, ActivityFeedRepository {
    List<Post> findAllByUserOrderByCreatedDesc(User user);
}
