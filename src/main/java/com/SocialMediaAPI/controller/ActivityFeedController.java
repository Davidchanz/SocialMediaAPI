package com.SocialMediaAPI.controller;

import com.SocialMediaAPI.dto.ActivityFeedDto;
import com.SocialMediaAPI.dto.ApiErrorDto;
import com.SocialMediaAPI.dto.ApiResponseSingleOk;
import com.SocialMediaAPI.dto.PostDto;
import com.SocialMediaAPI.model.Post;
import com.SocialMediaAPI.model.User;
import com.SocialMediaAPI.service.ActivityFeedService;
import com.SocialMediaAPI.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    private ActivityFeedService activityFeedService;

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
                            array = @ArraySchema(schema = @Schema(implementation = ApiResponseSingleOk.class))) }
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Parametr page is not valid",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ApiErrorDto.class))) }
            ),
            @ApiResponse(responseCode = "401", description = "Un-Authorized user", content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })

    @GetMapping("/feed")
    public ResponseEntity<?> getActivityFeedPageable(@RequestParam(value = "page", required = false) String page, Principal principal){
        User user = userService.findUserByUserName(principal.getName());

        Pageable pageable;
        if(page == null)
            pageable = PageRequest.of(0, pageSize);
        else {
            try {
                int pageNum = Integer.parseInt(page);
                if(pageNum < 1)
                    throw new NumberFormatException("");
                int maxPageNumber = activityFeedService.getMaxPageNumber(user, pageSize);
                if(pageNum > maxPageNumber)
                    pageNum = maxPageNumber;
                pageable = PageRequest.of(pageNum, pageSize);
            } catch (IllegalArgumentException ex) {
                return new ResponseEntity<>(new ApiErrorDto(HttpStatus.BAD_REQUEST, "/feed", ex.getClass().getName(), ex.getMessage()), HttpStatus.BAD_REQUEST);
            }
        }

        List<Post> activityFeed = activityFeedService.findAllActivityFeedPostsOrderByCreatedDescPageable(user, pageable);

        if(activityFeed.isEmpty())
            return new ResponseEntity<>(new ApiResponseSingleOk("Activity Feed", "Your activity feed is empty."), HttpStatus.NOT_FOUND);
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
                            array = @ArraySchema(schema = @Schema(implementation = ApiResponseSingleOk.class))) }
            ),
            @ApiResponse(responseCode = "401", description = "Un-Authorized user", content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })

    @GetMapping("/feed/all")
    public ResponseEntity<?> getActivityFeedAll(Principal principal){
        User user = userService.findUserByUserName(principal.getName());

        List<Post> activityFeed = activityFeedService.findAllActivityFeedPostsOrderByCreatedDesc(user);

        if(activityFeed.isEmpty())
            return new ResponseEntity<>(new ApiResponseSingleOk("Activity Feed", "Your activity feed is empty."), HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>(ActivityFeedDto.builder()
                .feed(activityFeed.stream()
                        .map(PostDto::createPostDto)
                        .collect(Collectors.toList()))
                .build(), HttpStatus.OK);
    }

}
