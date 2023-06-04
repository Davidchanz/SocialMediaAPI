package com.SocialMediaAPI.repository;

import com.SocialMediaAPI.model.Post;
import com.SocialMediaAPI.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityFeedRepository {

    List<Post> findAllActivityFeedPostsOrderByCreatedDesc(User user);

    List<Post> findAllActivityFeedPostsOrderByCreatedDescPageable(User user, Pageable pageable);

    int getMaxPageNumber(User user, int maxPageSize);
}
