package com.SocialMediaAPI.dto;

import com.SocialMediaAPI.model.Image;
import com.SocialMediaAPI.model.Post;
import com.SocialMediaAPI.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class PostDto {
    private Long id = null;

    private String header;

    private String text;

    private Instant created = null;

    private List<ImageDto> images = new ArrayList<>();

    private UserDto user = null;

    public static PostDto createPostDto(Post post){
        PostDto postDto = new PostDto();
        postDto.setId(post.getId());
        postDto.setHeader(post.getHeader());
        postDto.setText(post.getText());
        postDto.setCreated(post.getCreated());
        postDto.setUser((UserDto) UserDto.createUserDto(post.getUser()));
        postDto.setImages(post.getImages()
                .stream()
                .map(ImageDto::createImageDto)
                .collect(Collectors.toList())
        );
        return postDto;
    }
}
