package com.SocialMediaAPI.service;

import com.SocialMediaAPI.dto.PostDto;
import com.SocialMediaAPI.exception.PostNotFoundException;
import com.SocialMediaAPI.model.Image;
import com.SocialMediaAPI.model.Post;
import com.SocialMediaAPI.model.User;
import com.SocialMediaAPI.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public List<Post> findAllByUser(User user) {
        return postRepository.findAllByUser(user);
    }

    public void savePost(Post post) {
        postRepository.save(post);
    }
}
