package com.SocialMediaAPI.controller;

import com.SocialMediaAPI.config.TestConfig;
import com.SocialMediaAPI.configuration.SecurityConfig;
import com.SocialMediaAPI.dto.PostDto;
import com.SocialMediaAPI.model.CustomUserDetails;
import com.SocialMediaAPI.model.Image;
import com.SocialMediaAPI.model.Post;
import com.SocialMediaAPI.model.User;
import com.SocialMediaAPI.security.JwtAuthenticationEntryPoint;
import com.SocialMediaAPI.security.JwtTokenProvider;
import com.SocialMediaAPI.service.*;
import com.SocialMediaAPI.utils.ImageUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@WebMvcTest({ PostController.class })
@Import({ SecurityConfig.class, TestConfig.class })
class PostControllerTest extends AbstractTest{
    @InjectMocks
    PostController postController;

    @MockBean
    PostService postService;

    @MockBean
    ImageService imageService;

    @MockBean
    UserService userService;

    @BeforeEach
    public void setUp() {
        super.setUp(postController);
    }

    @Test
    void createPost() throws Exception {
        MockMultipartFile file = new MockMultipartFile("image", "Kartman.png", MediaType.IMAGE_PNG_VALUE, new FileInputStream("src/main/resources/static/Kartman.png").readAllBytes());
        Image image = new Image();
        image.setType(file.getContentType());
        image.setImageData(ImageUtils.compressImage(file.getBytes()));
        image.setName(file.getOriginalFilename());

        when(principal.getName()).thenReturn(user.getUsername());
        when(userService.findUserByUserName(any(String.class))).thenReturn(user);
        when(imageService.uploadImage(any(MultipartFile.class))).thenReturn(image);
        when(postService.createPost(any(PostDto.class), any(List.class), any(User.class))).thenReturn(post);


        MockPart coverPart = new MockPart("image", "Kartman.png", Files.readAllBytes(Paths.get("src/main/resources/static/Kartman.png")));
        coverPart.getHeaders().setContentType(MediaType.IMAGE_PNG);

        MockPart coverPart2 = new MockPart("post", "post.json", Files.readAllBytes(Paths.get("src/main/resources/static/post.json")));
        coverPart2.getHeaders().setContentType(APPLICATION_JSON_UTF8);

        this.mvc.perform(multipart("/createPost")
                        .part(coverPart, coverPart2)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    void changePost() throws Exception {
        MockMultipartFile file = new MockMultipartFile("image", "Kartman.png", MediaType.IMAGE_PNG_VALUE, new FileInputStream("src/main/resources/static/Kartman.png").readAllBytes());
        Image image = new Image();
        image.setType(file.getContentType());
        image.setImageData(ImageUtils.compressImage(file.getBytes()));
        image.setName(file.getOriginalFilename());

        when(principal.getName()).thenReturn("admin");
        when(userService.findUserByUserName("admin")).thenReturn(user);
        when(imageService.uploadImage(any(MultipartFile.class))).thenReturn(image);
        when(postService.createPost(any(PostDto.class), any(List.class), any(User.class))).thenReturn(post);
        when(postService.findPostById(any(Long.class))).thenReturn(post);
        when(postService.savePost(any(Post.class))).thenReturn(post);

        MockPart coverPart = new MockPart("image", "Kartman.png", Files.readAllBytes(Paths.get("src/main/resources/static/Kartman.png")));
        coverPart.getHeaders().setContentType(MediaType.IMAGE_PNG);

        MockPart coverPart2 = new MockPart("post", "post.json", Files.readAllBytes(Paths.get("src/main/resources/static/post.json")));
        coverPart2.getHeaders().setContentType(APPLICATION_JSON_UTF8);

        MockPart coverPart3 = new MockPart("postId", "1".getBytes());

        this.mvc.perform(multipart("/changePost")
                        .part(coverPart, coverPart2, coverPart3)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    void deletePost() throws Exception {
        when(principal.getName()).thenReturn("admin");
        when(userService.findUserByUserName("admin")).thenReturn(user);
        when(postService.findPostById(any(Long.class))).thenReturn(post);

        this.mvc.perform(post("/deletePost")
                        .param("postId", "1")
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    void showPost() throws Exception {
        when(postService.findPostById(any(Long.class))).thenReturn(post);

        this.mvc.perform(get("/post")
                        .param("postId", "1")
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    void showAllUsersPost() throws Exception {
        when(userService.findUserByUserName("admin")).thenReturn(user);
        when(postService.findAllByUserOrderByCreatedDesc(any(User.class))).thenReturn(new ArrayList<>());

        this.mvc.perform(get("/user/posts")
                        .param("username", "admin")
                        .headers(headers))
                .andExpect(status().isOk());
    }
}