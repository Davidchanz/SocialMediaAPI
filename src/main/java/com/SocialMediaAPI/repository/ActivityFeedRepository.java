package com.SocialMediaAPI.repository;

import com.SocialMediaAPI.model.Post;
import com.SocialMediaAPI.model.User;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ActivityFeedRepository {

    List<Post> findAllActivityFeedPostsOrderByCreatedDesc(User user);

    List<Post> findAllActivityFeedPostsOrderByCreatedDescPageable(User user, Pageable pageable);
}
