package com.SocialMediaAPI.controller;

import com.SocialMediaAPI.dto.PostDto;
import com.SocialMediaAPI.dto.TokenDto;
import com.SocialMediaAPI.model.Image;
import com.SocialMediaAPI.model.Post;
import com.SocialMediaAPI.model.User;
import com.SocialMediaAPI.service.ImageService;
import com.SocialMediaAPI.service.PostService;
import com.SocialMediaAPI.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;

@RestController
public class PostController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private ImageService imageService;

    @Operation(summary = "Create new Post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get created Post",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Post.class)) })})

    @PostMapping("/createPost")
    public Post createPost(@RequestPart(value = "image[]", required = false) MultipartFile[] files, @RequestPart(value = "post") PostDto postDto, Principal principal) throws IOException {//TODO IOException
        User user = userService.findUserByUserName(principal.getName());

        ArrayList<Image> images = new ArrayList<>();
        if(files != null)
            for(var file: files) {
                images.add(imageService.uploadImage(file));
            }

        return postService.createPost(postDto, images, user);
    }

}
