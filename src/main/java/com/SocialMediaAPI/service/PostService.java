package com.SocialMediaAPI.service;

import com.SocialMediaAPI.dto.PostDto;
import com.SocialMediaAPI.exception.PostNotFoundException;
import com.SocialMediaAPI.model.Image;
import com.SocialMediaAPI.model.Post;
import com.SocialMediaAPI.model.User;
import com.SocialMediaAPI.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public Post createPost(PostDto postDto, List<Image> images, User user){
        Post post = Post.builder()
                .header(postDto.getHeader())
                .text(postDto.getText())
                .user(user)
                .images(images)
                .build();
        return postRepository.save(post);
    }

    public Post findPostById(Long id){
        return postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("Post with id: " + id + " not found."));
    }

    public void deletePost(Post post) {
        postRepository.deleteById(post.getId());
    }

    public List<Post> findAllByUserOrderByCreatedDesc(User user) {
        return postRepository.findAllByUserOrderByCreatedDesc(user);
    }

    public Post savePost(Post post) {
        return postRepository.save(post);
    }

    public List<Post> findAllActivityFeedPostsOrderByCreatedDesc(User user) {
        return postRepository.findAllActivityFeedPostsOrderByCreatedDesc(user);
    }

    public List<Post> findAllActivityFeedPostsOrderByCreatedDescPageable(User user, Pageable page) {
        return postRepository.findAllActivityFeedPostsOrderByCreatedDescPageable(user, page);
    }
}
