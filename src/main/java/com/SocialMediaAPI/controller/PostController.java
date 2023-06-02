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
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
import java.util.Objects;
import java.util.List;

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
                            schema = @Schema(implementation = Post.class)) }
            )
    })

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

    @Operation(summary = "Delete post by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post was deleted",
                    content = { @Content(mediaType = "text/plain",
                            examples = @ExampleObject("Post with id: post_id was deleted!")) }
            ),
            @ApiResponse(responseCode = "405", description = "Not allowed delete this Post",
                    content = { @Content(mediaType = "text/plain",
                            examples = @ExampleObject("You can delete only your own posts!")) }
            )
    })

    @PostMapping("/deletePost")
    public ResponseEntity<?> deletePost(@RequestParam(value = "id") String id, Principal principal){
        User user = userService.findUserByUserName(principal.getName());

        Post post = postService.findPostById(Long.parseLong(id));
        if(Objects.equals(post.getUser().getId(), user.getId())){
            postService.deletePost(post);
            return new ResponseEntity<>("Post with id: " + id + " was deleted!", HttpStatus.OK);
        }else
            return new ResponseEntity<>("You can delete only your own posts!", HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Operation(summary = "Get post by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Show Post",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Post.class)) }
            )
    })

    @GetMapping("/post")
    public Post showPost(@RequestParam(value = "id") String id){
        return postService.findPostById(Long.parseLong(id));
    }

    @Operation(summary = "Get all posts from user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Show All user posts",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Post.class))) }
            )
    })

    @GetMapping("/user/posts")
    public List<Post> showAllUsersPost(@RequestParam(value = "username") String username){
        User user = userService.findUserByUserName(username);
        return postService.findAllByUser(user);
    }

    @Operation(summary = "Change Post, change images, header, text or nothing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post was changed",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Post.class)) }
            ),
            @ApiResponse(responseCode = "405", description = "Not allowed change this post",
                    content = { @Content(mediaType = "text/plain",
                            examples = @ExampleObject("You can change only your own posts!")) }
            )
    })

    @PutMapping("/changePost")
    public ResponseEntity<?> changePost(@RequestPart(value = "id") String id, @RequestPart(value = "image[]", required = false) MultipartFile[] files, @RequestPart(value = "post") PostDto postDto, Principal principal) throws IOException {//TODO Exception
        User user = userService.findUserByUserName(principal.getName());

        Post post = postService.findPostById(Long.parseLong(id));
        if(Objects.equals(post.getUser().getId(), user.getId())){
            post.setImages(new ArrayList<>());
            postService.savePost(post);
            for(var image: post.getImages()) {
                imageService.deleteImage(image);//delete old images
            }
            ArrayList<Image> images = new ArrayList<>();
            if(files != null)
                for(var file: files) {
                    images.add(imageService.uploadImage(file));//load new images
                }
            post.setHeader(postDto.getHeader());
            post.setText(postDto.getText());
            post.setImages(images);
            postService.savePost(post);

            return new ResponseEntity<>(post, HttpStatus.OK);
        }else
            return new ResponseEntity<>("You can change only your own post.", HttpStatus.METHOD_NOT_ALLOWED);
    }

}
