package com.SocialMediaAPI.dto;

import com.SocialMediaAPI.model.Image;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class ImageDto {

    @NotNull
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String type;

    public static ImageDto getDecompressedImage(Image image){
        return ImageDto.builder()
                .id(image.getId())
                .name(image.getName())
                .type(image.getType())
                .build();
    }

}
