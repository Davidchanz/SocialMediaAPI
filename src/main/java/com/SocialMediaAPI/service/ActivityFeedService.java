package com.SocialMediaAPI.service;

import com.SocialMediaAPI.model.Post;
import com.SocialMediaAPI.model.User;
import com.SocialMediaAPI.repository.ActivityFeedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityFeedService {

    @Autowired
    private ActivityFeedRepository activityFeedRepository;

    public List<Post> findAllActivityFeedPostsOrderByCreatedDesc(User user) {
        return activityFeedRepository.findAllActivityFeedPostsOrderByCreatedDesc(user);
    }

    public List<Post> findAllActivityFeedPostsOrderByCreatedDescPageable(User user, Pageable page) {
        return activityFeedRepository.findAllActivityFeedPostsOrderByCreatedDescPageable(user, page);
    }

    public int getMaxPageNumber(User user, int maxPageSize){
        return activityFeedRepository.getMaxPageNumber(user, maxPageSize);
    }
}
