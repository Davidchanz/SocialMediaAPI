package com.SocialMediaAPI.dto;

import com.SocialMediaAPI.model.Image;
import com.SocialMediaAPI.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class PostDto {

    @NotNull
    private String header;

    @NotNull
    private String text;
}
