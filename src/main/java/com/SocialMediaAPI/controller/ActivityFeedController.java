package com.SocialMediaAPI.controller;

import com.SocialMediaAPI.dto.ActivityFeedDto;
import com.SocialMediaAPI.dto.ApiResponse;
import com.SocialMediaAPI.dto.PostDto;
import com.SocialMediaAPI.model.Post;
import com.SocialMediaAPI.model.User;
import com.SocialMediaAPI.service.PostService;
import com.SocialMediaAPI.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ActivityFeedController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Value("${pageSize}")
    private int pageSize;

    @Operation(summary = "Get activity feed pageable and sorting")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Get activity feed on concrete page",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ActivityFeedDto.class))) }
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Activity feed is empty for you",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ApiResponse.class))) }
            )
    })

    @GetMapping("/feed")
    public ResponseEntity<?> getActivityFeedPageable(@RequestParam(value = "page", required = false) String page, Principal principal){
        User user = userService.findUserByUserName(principal.getName());

        Pageable pageable;
        if(page == null)
            pageable = PageRequest.of(0, pageSize);
        else
            pageable = PageRequest.of(Integer.parseInt(page) - 1, pageSize);//TODO page check error

        List<Post> activityFeed;
        activityFeed = postService.findAllActivityFeedPostsOrderByCreatedDescPageable(user, pageable);

        if(activityFeed.isEmpty())
            return new ResponseEntity<>(new ApiResponse("Your activity feed is empty."), HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>(ActivityFeedDto.builder()
                .feed(activityFeed.stream()
                        .map(PostDto::createPostDto)
                        .collect(Collectors.toList()))
                .page(pageable.getPageNumber())
                .build(), HttpStatus.OK);
    }

    @Operation(summary = "Get all activity feed sorting")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Get activity feed on all pages",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ActivityFeedDto.class))) }
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Activity feed is empty for you",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ApiResponse.class))) }
            )
    })

    @GetMapping("/feed/all")
    public ResponseEntity<?> getActivityFeedAll(Principal principal){
        User user = userService.findUserByUserName(principal.getName());

        List<Post> activityFeed = postService.findAllActivityFeedPostsOrderByCreatedDesc(user);

        if(activityFeed.isEmpty())
            return new ResponseEntity<>(new ApiResponse("Your activity feed is empty."), HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>(ActivityFeedDto.builder()
                .feed(activityFeed.stream()
                        .map(PostDto::createPostDto)
                        .collect(Collectors.toList()))
                .build(), HttpStatus.OK);
    }

}
