package com.SocialMediaAPI.controller;

import com.SocialMediaAPI.dto.ApiErrorDto;
import com.SocialMediaAPI.dto.ApiResponseSingleOk;
import com.SocialMediaAPI.dto.NotificationDto;
import com.SocialMediaAPI.dto.UserDto;
import com.SocialMediaAPI.exception.RepeatActionException;
import com.SocialMediaAPI.exception.ResourceNotFoundException;
import com.SocialMediaAPI.model.*;
import com.SocialMediaAPI.service.ChatService;
import com.SocialMediaAPI.service.NotificationService;
import com.SocialMediaAPI.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.MethodNotAllowedException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Objects;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ChatService chatService;

    @Operation(summary = "Send Friend Invite to User")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Invite was sent to user",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseSingleOk.class)) }),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "You can not sent invite to your self",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDto.class)) }),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "You already sent invite to this user or this user already sent invite to you",
                    content = { @Content(mediaType = "application/json",
            schema = @Schema(implementation = ApiErrorDto.class)) }),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "You are already a friend with user",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDto.class)) }),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User by username not found.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDto.class)) }),
            @ApiResponse(responseCode = "401", description = "Un-Authorized user", content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })

    @PostMapping("/friend/invite")
    public ResponseEntity<?> sendFriendInvite(@RequestParam(value = "username") String username, Principal principal){
        User user = userService.findUserByUserName(principal.getName());
        User potentialFriend = userService.findUserByUserName(username);

        if(user.getFriends().contains(potentialFriend))
            return new ResponseEntity<>(new ApiErrorDto(HttpStatus.CONFLICT, "/friend/invite", RepeatActionException.class.getName(), "You are already a friend with " + potentialFriend.getUsername()), HttpStatus.CONFLICT);

        if(Objects.equals(user.getId(), potentialFriend.getId()))
            return new ResponseEntity<>(new ApiErrorDto(HttpStatus.CONFLICT, "/friend/invite", RepeatActionException.class.getName(), "You can't be a friend with your self!"), HttpStatus.CONFLICT);

        if(!notificationService.isInviteExist(user, potentialFriend)
                && !notificationService.isInviteExist(potentialFriend, user)) {
            Notification friendInvite = new Notification();
            friendInvite.setMessage("Hello! I am " + user.getUsername() + ". Let become friends!)");
            friendInvite.setFrom(user);
            friendInvite.setTo(potentialFriend);
            friendInvite.setType(NotificationType.FRIEND_INVITE);

            friendInvite = notificationService.createNotification(friendInvite);
            userService.sendNotification(friendInvite);

            userService.subscribe(user, potentialFriend);

            return new ResponseEntity<>(
                    new ApiResponseSingleOk("Send Friend Invite", "User: "
                            + user.getUsername()
                            + " sent friend invite to user: "
                            + potentialFriend.getUsername()),
                    HttpStatus.OK);
        }else {
            return new ResponseEntity<>(
                    new ApiErrorDto(HttpStatus.CONFLICT, "/friend/invite", RepeatActionException.class.getName(), "User: "
                            + user.getUsername()
                            + " already sent friend invite to user: "
                            + potentialFriend.getUsername()),
                    HttpStatus.CONFLICT);
        }
    }

    @Operation(summary = "Reject Friend Invite")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Invite was rejected user who sent invite continues been subscriber",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseSingleOk.class)) }),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "inviteId param is not valid number.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDto.class)) }),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Invite by inviteId not found.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDto.class)) }),
            @ApiResponse(responseCode = "401", description = "Un-Authorized user", content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })

    @PostMapping("/friend/invite/reject")
    public ResponseEntity<?> rejectFriendInvite(@RequestParam(value = "inviteId") String id, Principal principal){
        User user = userService.findUserByUserName(principal.getName());

        long inviteId;
        try{
            inviteId = Long.parseLong(id);
            if(inviteId < 0)
                throw new NumberFormatException();
        }catch (NumberFormatException ex){
            return new ResponseEntity<>(new ApiErrorDto(HttpStatus.BAD_REQUEST, "/friend/invite/reject", NumberFormatException.class.getName(), "inviteId param is not valid number."), HttpStatus.BAD_REQUEST);
        }
        Notification notification = notificationService.findNotificationById(inviteId);

        if(Objects.equals(notification.getTo().getId(), user.getId())) {
            userService.removeNotification(user, notification);
            notificationService.deleteNotification(notification);
            return new ResponseEntity<>(new ApiResponseSingleOk("Reject Friend Invite", "Invite rejected!"), HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(new ApiErrorDto(HttpStatus.NOT_FOUND, "/friend/invite/reject", ResourceNotFoundException.class.getName(), "Invite with id: " + id + " not found!"), HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Accept Friend Invite and create Chat with him")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Invite was accepted you became subscriber and friend with user who sent invite",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseSingleOk.class)) }),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "inviteId param is not valid number.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDto.class)) }),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Invite by inviteId not found.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDto.class)) }),
            @ApiResponse(responseCode = "401", description = "Un-Authorized user", content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })

    @PostMapping("/friend/invite/accept")
    public ResponseEntity<?> acceptFriendInvite(@RequestParam(value = "inviteId") String id, Principal principal){
        User user = userService.findUserByUserName(principal.getName());

        long inviteId;
        try{
            inviteId = Long.parseLong(id);
            if(inviteId < 0)
                throw new NumberFormatException();
        }catch (NumberFormatException ex){
            return new ResponseEntity<>(new ApiErrorDto(HttpStatus.BAD_REQUEST, "/friend/invite/accept", NumberFormatException.class.getName(), "inviteId param is not valid number."), HttpStatus.BAD_REQUEST);
        }

        Notification notification = notificationService.findNotificationById(inviteId);
        User from = (User)notification.getFrom();

        if(Objects.equals(notification.getTo().getId(), user.getId())) {
            userService.subscribe(user, from);
            userService.becomeFriend(user, from);

            Chat chat = new Chat();
            chat.setName("(" + from.getUsername() + ")_(" + user.getUsername() + ")");
            chat.getUsers().add(from);
            chat.getUsers().add(user);
            chat.setType(ChatType.PRIVATE);
            chat = chatService.createNewChat(chat);
            userService.addChatToUser(from, chat);
            userService.addChatToUser(user, chat);

            userService.removeNotification(user, notification);
            notificationService.deleteNotification(notification);

            return new ResponseEntity<>(new ApiResponseSingleOk("Accepted Friend Invite", "Invite accepted!"), HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(new ApiErrorDto(HttpStatus.NOT_FOUND, "/friend/invite/accept", ResourceNotFoundException.class.getName(), "Invite with id: " + id + " not found!"), HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Subscribe on User")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "You successfully subscribed on user",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseSingleOk.class)) }),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "You already subscribed on user",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDto.class)) }),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "You can not subscribe on your self",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDto.class)) }),
            @ApiResponse(responseCode = "401", description = "Un-Authorized user", content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestParam(value = "username") String username, Principal principal){
        User user = userService.findUserByUserName(principal.getName());
        User publisher = userService.findUserByUserName(username);

        if(user == publisher)
            return new ResponseEntity<>(new ApiErrorDto(HttpStatus.CONFLICT, "/subscribe", MethodNotAllowedException.class.getName(), "You can not subscribe on your self"), HttpStatus.CONFLICT);
        if(!user.getPublishers().contains(publisher)){
            userService.subscribe(user, publisher);
            return new ResponseEntity<>(new ApiResponseSingleOk("Subscribe", "You subscribed on " + publisher.getUsername()), HttpStatus.OK);
        }else
            return new ResponseEntity<>(new ApiErrorDto(HttpStatus.CONFLICT, "/subscribe", RepeatActionException.class.getName(), "You already subscribed on " + publisher.getUsername()), HttpStatus.CONFLICT);
    }

    @Operation(summary = "UnSubscribe from User")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "You successfully unSubscribed from user",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseSingleOk.class)) }),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "You was not subscribed on user",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDto.class)) }),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "You can not unSubscribe from your self",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDto.class)) }),
            @ApiResponse(responseCode = "401", description = "Un-Authorized user", content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })

    @PostMapping("/unSubscribe")
    public ResponseEntity<?> unSubscribe(@RequestParam(value = "username") String username, Principal principal){
        User user = userService.findUserByUserName(principal.getName());
        User publisher = userService.findUserByUserName(username);

        if(user == publisher)
            return new ResponseEntity<>(new ApiErrorDto(HttpStatus.CONFLICT, "/subscribe", MethodNotAllowedException.class.getName(), "You can not unSubscribe from your self"), HttpStatus.CONFLICT);
        if(user.getPublishers().contains(publisher)){
            userService.unSubscribe(user, publisher);
            return new ResponseEntity<>(new ApiResponseSingleOk("UnSubscribe", "You unsubscribed from " + publisher.getUsername()), HttpStatus.OK);
        }else
            return new ResponseEntity<>(new ApiErrorDto(HttpStatus.CONFLICT, "/unSubscribe", MethodNotAllowedException.class.getName(), "You are not subscribed on " + publisher.getUsername() + " yet"), HttpStatus.CONFLICT);
    }

    @Operation(summary = "Remove User from Friend list and remove Chat with him")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "You removed user from your friend list and publisher list",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseSingleOk.class)) }),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "You are not friend with user",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDto.class)) }),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User by username not found.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDto.class)) }),
            @ApiResponse(responseCode = "401", description = "Un-Authorized user", content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })

    @PostMapping("/friend/remove")
    public ResponseEntity<?> removeFriend(@RequestParam(value = "username") String username, Principal principal){
        User user = userService.findUserByUserName(principal.getName());
        User friend = userService.findUserByUserName(username);

        if(user.getFriends().contains(friend)){
            userService.deleteFriend(user, friend);
            chatService.deleteChat(user, friend);
            return new ResponseEntity<>(new ApiResponseSingleOk("Remove Friend", "You removed " + friend.getUsername() + " from your friend list and publisher list"), HttpStatus.OK);
        }else
            return new ResponseEntity<>(new ApiErrorDto(HttpStatus.CONFLICT, "/friend/remove", MethodNotAllowedException.class.getName(), "You are not friend with " + friend.getUsername()), HttpStatus.CONFLICT);
    }

    @Operation(summary = "Get all user's notifications")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "You removed user from your friend list and publisher list",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema( schema = @Schema( implementation = NotificationDto.class))) }),
            @ApiResponse(responseCode = "401", description = "Un-Authorized user", content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })

    @GetMapping("/notifications")
    public ResponseEntity<?> getNotifications(Principal principal){
        User user = userService.findUserByUserName(principal.getName());
        List<NotificationDto> resultNotificationList = new ArrayList<>();
        var notificationList = notificationService.findAllNotificationByUser(user);
        for(var notification: notificationList){
            resultNotificationList.add(NotificationDto
                    .builder()
                            .id(notification.getId())
                            .message(notification.getMessage())
                            .type(notification.getType())
                            .from(UserDto.createUserDto(notification.getFrom()))
                            .to((UserDto) UserDto.createUserDto(notification.getTo()))
                    .build());
        }
        return new ResponseEntity<>(resultNotificationList, HttpStatus.OK);
    }

}









