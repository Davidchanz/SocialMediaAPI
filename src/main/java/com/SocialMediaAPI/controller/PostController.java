package com.SocialMediaAPI.controller;

import com.SocialMediaAPI.dto.ApiErrorDto;
import com.SocialMediaAPI.dto.PostDto;
import com.SocialMediaAPI.exception.WrongOwnerException;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Objects;

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
                            schema = @Schema(implementation = PostDto.class)) }
            )
    })

    @PostMapping("/createPost")
    public ResponseEntity<?> createPost(@RequestPart(value = "image", required = false) MultipartFile[] files, @RequestPart(value = "post", required = false) PostDto postDto, Principal principal) {

        if(postDto == null)
            return new ResponseEntity<>(new ApiErrorDto(HttpStatus.BAD_REQUEST, "/createPost", MultipartException.class.getName(), "Required parameter: 'post' is missing!"), HttpStatus.BAD_REQUEST);

        User user = userService.findUserByUserName(principal.getName());

        ArrayList<Image> images = new ArrayList<>();
        if(files != null)
            for(int i = 0; i < files.length; i++) {
                try {
                    images.add(imageService.uploadImage(files[i]));
                }catch (NullPointerException ex){
                    return new ResponseEntity<>(new ApiErrorDto(HttpStatus.BAD_REQUEST, "/createPost", NullPointerException.class.getName(), "Can not read File number: '" + ++i + "' file is NULL!"), HttpStatus.BAD_REQUEST);
                }
                catch (IOException ex){
                    return new ResponseEntity<>(new ApiErrorDto(HttpStatus.BAD_REQUEST, "/createPost", IOException.class.getName(), "Can not read File number: '" + files[i].getOriginalFilename() + "' file is damaged!"), HttpStatus.BAD_REQUEST);
                }
            }

        Post post = postService.createPost(postDto, images, user);
        return new ResponseEntity<>(PostDto.createPostDto(post), HttpStatus.OK);
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
    public ResponseEntity<?> deletePost(@RequestParam(value = "postId") String id, Principal principal){
        User user = userService.findUserByUserName(principal.getName());

        long postId;
        try{
            postId = Long.parseLong(id);
            if(postId < 0)
                throw new NumberFormatException();
        }catch (NumberFormatException ex){
            return new ResponseEntity<>(new ApiErrorDto(HttpStatus.BAD_REQUEST, "/deletePost", NumberFormatException.class.getName(), "postId param is not valid number."), HttpStatus.BAD_REQUEST);
        }
        Post post = postService.findPostById(postId);
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
                            schema = @Schema(implementation = PostDto.class)) }
            )
    })

    @GetMapping("/post")
    public ResponseEntity<?> showPost(@RequestParam(value = "postId") String id){
        long postId;
        try{
            postId = Long.parseLong(id);
            if(postId < 0)
                throw new NumberFormatException();
        }catch (NumberFormatException ex){
            return new ResponseEntity<>(new ApiErrorDto(HttpStatus.BAD_REQUEST, "/post", NumberFormatException.class.getName(), "postId param is not valid number."), HttpStatus.BAD_REQUEST);
        }
        Post post = postService.findPostById(postId);
        return new ResponseEntity<>(PostDto.createPostDto(post), HttpStatus.OK);
    }

    @Operation(summary = "Get all posts from user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Show All user posts",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PostDto.class))) }
            )
    })

    @GetMapping("/user/posts")
    public ResponseEntity<?> showAllUsersPost(@RequestParam(value = "username") String username){
        User user = userService.findUserByUserName(username);
        var posts = postService.findAllByUserOrderByCreatedDesc(user);

        return new ResponseEntity<>(posts
                .stream()
                .map(PostDto::createPostDto)
        , HttpStatus.OK);
    }

    @Operation(summary = "Change Post, change images, header, text or nothing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post was changed",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PostDto.class)) }
            ),
            @ApiResponse(responseCode = "405", description = "Not allowed change this post",
                    content = { @Content(mediaType = "text/plain",
                            examples = @ExampleObject("You can change only your own posts!")) }
            )
    })

    @PostMapping("/changePost")
    public ResponseEntity<?> changePost(@RequestPart(value = "postId", required = false) String id, @RequestPart(value = "image", required = false) MultipartFile[] files, @RequestPart(value = "post", required = false) PostDto postDto, Principal principal) {
        User user = userService.findUserByUserName(principal.getName());

        if(postDto == null)
            return new ResponseEntity<>(new ApiErrorDto(HttpStatus.BAD_REQUEST, "/createPost", MultipartException.class.getName(), "Required parameter: 'post' is missing!"), HttpStatus.BAD_REQUEST);
        if(id == null)
            return new ResponseEntity<>(new ApiErrorDto(HttpStatus.BAD_REQUEST, "/createPost", MultipartException.class.getName(), "Required parameter: 'postId' is missing!"), HttpStatus.BAD_REQUEST);

        long postId;
        try{
            postId = Long.parseLong(id);
            if(postId < 0)
                throw new NumberFormatException();
        }catch (NumberFormatException ex){
            return new ResponseEntity<>(new ApiErrorDto(HttpStatus.BAD_REQUEST, "/changePost", NumberFormatException.class.getName(), "postId param is not valid number."), HttpStatus.BAD_REQUEST);
        }
        Post post = postService.findPostById(postId);
        if(Objects.equals(post.getUser().getId(), user.getId())){
            post.setImages(new ArrayList<>());
            postService.savePost(post);
            for(var image: post.getImages()) {
                imageService.deleteImage(image);//delete old images
            }
            ArrayList<Image> images = new ArrayList<>();
            if(files != null)
                for(int i = 0; i < files.length; i++) {
                    try{
                        images.add(imageService.uploadImage(files[i]));//load new images
                    }catch (NullPointerException ex){
                        return new ResponseEntity<>(new ApiErrorDto(HttpStatus.BAD_REQUEST, NullPointerException.class.getName(), "/changePost", "Can not read File number: '" + ++i + "' file is NULL!"), HttpStatus.BAD_REQUEST);
                    }
                    catch (IOException ex){
                        return new ResponseEntity<>(new ApiErrorDto(HttpStatus.BAD_REQUEST, "/changePost", IOException.class.getName(), "Can not read File number: '" + files[i].getOriginalFilename() + "' file is damaged!"), HttpStatus.BAD_REQUEST);
                    }
                }
            post.setHeader(postDto.getHeader());
            post.setText(postDto.getText());
            post.setImages(images);
            post = postService.savePost(post);

            return new ResponseEntity<>(PostDto.createPostDto(post), HttpStatus.OK);
        }else
            return new ResponseEntity<>(new ApiErrorDto(HttpStatus.METHOD_NOT_ALLOWED, "Not Allowed action!", WrongOwnerException.class.getName(), "You can change only your own post."), HttpStatus.METHOD_NOT_ALLOWED);
    }

}
