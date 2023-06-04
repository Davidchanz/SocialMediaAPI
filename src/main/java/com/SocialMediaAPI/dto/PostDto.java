package com.SocialMediaAPI.dto;

import com.SocialMediaAPI.model.Image;
import com.SocialMediaAPI.model.Post;
import com.SocialMediaAPI.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class PostDto {
    @NotNull
    private Long id;

    @NotNull
    private String header;

    @NotNull
    private String text;

    @NotNull
    private Instant created;

    @NotNull
    private List<Image> images = new ArrayList<>();

    @NotNull
    private UserDto user;

    public static PostDto createPostDto(Post post){
        return PostDto.builder()
                .id(post.getId())
                .header(post.getHeader())
                .text(post.getText())
                .created(post.getCreated())
                .user((UserDto) UserDto.createUserDto(post.getUser()))
                .images(post.getImages())
                .build();
    }
}
