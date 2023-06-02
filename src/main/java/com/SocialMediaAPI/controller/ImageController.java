package com.SocialMediaAPI.controller;

import com.SocialMediaAPI.dto.ApiErrorDto;
import com.SocialMediaAPI.dto.TokenDto;
import com.SocialMediaAPI.model.Image;
import com.SocialMediaAPI.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class ImageController {

    @Autowired
    private ImageService imageService;

    @Operation(summary = "Upload images, max image size = 10MB, max request size = 50MB")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Images uploaded",
                    content = { @Content(mediaType = "text/plain",
                            examples = @ExampleObject("File: 'image_name.png' uploaded!")) })})

    @PostMapping("/uploadImage")
    public ResponseEntity<?> uploadImage(@RequestParam("image[]") MultipartFile[] files) throws IOException {
        String uploadImages = "";
        for(var file: files) {
            uploadImages += "File: " + imageService.uploadImage(file).getName() + " uploaded!";
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(uploadImages);
    }

    @Operation(summary = "Download image by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Images uploaded",
                    content = { @Content(mediaType = "image/png",
                            schema = @Schema(type = "string", format = "binary")) }
            )
    }
    )

    @GetMapping("/download")
    public ResponseEntity<?> downloadImage(@RequestParam("id") String id){
        byte[] imageData = imageService.downloadImage(Long.parseLong(id));
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(imageData);

    }

}
