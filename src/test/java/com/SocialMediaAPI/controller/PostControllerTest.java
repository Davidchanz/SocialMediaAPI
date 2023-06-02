package com.SocialMediaAPI.controller;

import com.SocialMediaAPI.config.TestConfig;
import com.SocialMediaAPI.configuration.SecurityConfig;
import com.SocialMediaAPI.dto.PostDto;
import com.SocialMediaAPI.model.Image;
import com.SocialMediaAPI.model.Post;
import com.SocialMediaAPI.model.User;
import com.SocialMediaAPI.service.ImageService;
import com.SocialMediaAPI.service.PostService;
import com.SocialMediaAPI.service.TokenService;
import com.SocialMediaAPI.utils.ImageUtils;
import jakarta.servlet.http.Part;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.security.core.parameters.P;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.Principal;
import java.util.List;

@WebMvcTest({ ImageController.class })
@Import({ SecurityConfig.class, TokenService.class, TestConfig.class })
@RunWith(SpringRunner.class)
class PostControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    ImageService imageService;

    @MockBean
    PostService postService;

    @Mock
    private Principal principal;

    @Disabled("TODO: Still need to work on it")
    @Test
    @WithMockUser
    void createPost() throws Exception {
        MockMultipartFile file = new MockMultipartFile("image[]", "Kartman.png", MediaType.IMAGE_PNG_VALUE, new FileInputStream("src/main/resources/static/Kartman.png").readAllBytes());
        Image image = new Image();
        image.setType(file.getContentType());
        image.setImageData(ImageUtils.compressImage(file.getBytes()));
        image.setName(file.getOriginalFilename());

        Post post = Post.builder()
                .build();

        when(imageService.uploadImage(any(MultipartFile.class))).thenReturn(image);
        when(postService.createPost(any(PostDto.class), any(List.class), any(User.class))).thenReturn(post);
        when(principal.getName()).thenReturn("GN");

        MockPart coverPart = new MockPart("image[]", "Kartman.png", Files.readAllBytes(Paths.get("src/main/resources/static/Kartman.png")));
        coverPart.getHeaders().setContentType(MediaType.IMAGE_PNG);

        MockPart coverPart2 = new MockPart("post", "post.json", Files.readAllBytes(Paths.get("src/main/resources/static/post.json")));
        coverPart2.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(multipart("/createPost")
                        .part(coverPart, coverPart2)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().string("File: Kartman.png uploaded!"));
    }
}