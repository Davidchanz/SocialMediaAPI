package com.SocialMediaAPI.controller;

import com.SocialMediaAPI.config.TestConfig;
import com.SocialMediaAPI.configuration.SecurityConfig;
import com.SocialMediaAPI.dto.LoginDto;
import com.SocialMediaAPI.model.CustomUserDetails;
import com.SocialMediaAPI.model.Image;
import com.SocialMediaAPI.repository.ImageRepository;
import com.SocialMediaAPI.security.JwtAuthenticationEntryPoint;
import com.SocialMediaAPI.security.JwtTokenProvider;
import com.SocialMediaAPI.service.AuthService;
import com.SocialMediaAPI.service.CustomUserDetailsService;
import com.SocialMediaAPI.service.ImageService;
import com.SocialMediaAPI.service.UserService;
import com.SocialMediaAPI.utils.ImageUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ ImageController.class })
@Import({ SecurityConfig.class, TestConfig.class })
class ImageControllerTest extends AbstractTest{
    @MockBean
    ImageService imageService;

    @InjectMocks
    ImageController imageController;

    @BeforeEach
    void setUp(){
        super.setUp(imageController);
    }
    @Test
    public void uploadImage() throws Exception {

        MockMultipartFile file = new MockMultipartFile("image", "Kartman.png", MediaType.IMAGE_PNG_VALUE, new FileInputStream("src/main/resources/static/Kartman.png").readAllBytes());
        Image image = new Image();
        image.setType(file.getContentType());
        image.setImageData(ImageUtils.compressImage(file.getBytes()));
        image.setName(file.getOriginalFilename());

        when(imageService.uploadImage(any(MultipartFile.class))).thenReturn(image);

        this.mvc.perform(multipart("/uploadImage")
                        .file(file)
                        .headers(headers))
                .andExpect(status().isOk());
    }

    @Test
    void downloadImage() throws Exception {
        MockMultipartFile file = new MockMultipartFile("image", "Kartman.png", MediaType.IMAGE_PNG_VALUE, new FileInputStream("src/main/resources/static/Kartman.png").readAllBytes());

        when(imageService.downloadImage(any(Long.class))).thenReturn(file.getBytes());

        this.mvc.perform(get("/download")
                        .param("imageId", "1")
                        .headers(headers))
                .andExpect(status().isOk())
                .andExpect(content().bytes(file.getBytes()));
    }
}