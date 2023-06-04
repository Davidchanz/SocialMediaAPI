package com.SocialMediaAPI.controller;

import com.SocialMediaAPI.dto.ApiErrorDto;
import com.SocialMediaAPI.dto.ApiResponseArrayOk;
import com.SocialMediaAPI.dto.ImageDto;
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
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile[] files) {
        ImageDto[] uploadImages = new ImageDto[files.length];
        for(int i = 0; i < files.length; i++) {
            try {
                uploadImages[i] = ImageDto.createImageDto(imageService.uploadImage(files[i]));
            }catch (NullPointerException ex){
                return new ResponseEntity<>(new ApiErrorDto(HttpStatus.BAD_REQUEST, "/uploadImage", "Can not read File number: '" + ++i + "' file is NULL!"), HttpStatus.BAD_REQUEST);
            }
            catch (IOException ex){
                return new ResponseEntity<>(new ApiErrorDto(HttpStatus.BAD_REQUEST, "/uploadImage", "Can not read File number: '" + files[i].getOriginalFilename() + "' file is damaged!"), HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(new ApiResponseArrayOk<>("Images Uploaded!", uploadImages), HttpStatus.OK);
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
    public ResponseEntity<?> downloadImage(@RequestParam("imageId") String id){
        long imageId;
        try{
            imageId = Long.parseLong(id);
            if(imageId < 0)
                throw new NumberFormatException();
        }catch (NumberFormatException ex){
            return new ResponseEntity<>(new ApiErrorDto(HttpStatus.BAD_REQUEST, "/download", "imageId param is not valid number."), HttpStatus.BAD_REQUEST);
        }
        byte[] imageData = imageService.downloadImage(imageId);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(imageData);
    }

}
