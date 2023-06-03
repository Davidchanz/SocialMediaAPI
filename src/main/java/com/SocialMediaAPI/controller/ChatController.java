package com.SocialMediaAPI.controller;

import com.SocialMediaAPI.dto.*;
import com.SocialMediaAPI.model.*;
import com.SocialMediaAPI.service.ChatMessageService;
import com.SocialMediaAPI.service.ChatService;
import com.SocialMediaAPI.service.NotificationService;
import com.SocialMediaAPI.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserService userService;

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private NotificationService notificationService;


    @Operation(summary = "Get All user's Chats")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Return user's Chats",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ChatDto.class))) })
    })

    @GetMapping("/chats")
    public ResponseEntity<?> getAllChats(Principal principal){
        User user = userService.findUserByUserName(principal.getName());//TODO error check

        List<ChatDto> resultChatList = new ArrayList<>();
        for(var chat: user.getChats()){
            resultChatList.add(ChatDto
                    .builder()
                            .id(chat.getId())
                            .name(chat.getName())
                            .users(chat.getUsers().stream()
                                    .map(UserDto::createUserDto)
                                    .map(sender -> (UserDto) sender)
                                    .collect(Collectors.toSet())
                            )
                    .build()
            );
        }

        return new ResponseEntity<>(resultChatList, HttpStatus.OK);
    }

    @Operation(summary = "Get Chat History")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Get Chat History",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ChatMessagesDto.class))) })
    })

    @GetMapping("/chat/history")
    public ResponseEntity<?> getChatMessageHistory(@RequestParam(value = "id") String id, Principal principal){
        User user = userService.findUserByUserName(principal.getName());//TODO error check
        Chat chat = chatService.findChatById(Long.parseLong(id));

        List<ChatMessagesDto> resultChatList = new ArrayList<>();
        for(var message:  chatMessageService.findByChatOrderByCreatedAsc(chat)) {
            resultChatList.add(ChatMessagesDto.createChatMessageDto(message));
        }

        return new ResponseEntity<>(resultChatList, HttpStatus.OK);
    }

    @Operation(summary = "Send message in chat")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Message was send, other users in chat received notification",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)) })
    })

    @PostMapping("/chat/send")
    public ResponseEntity<?> sendMessage(@RequestParam(value = "message") String message, @RequestParam(value = "id") String id, Principal principal){
        User user = userService.findUserByUserName(principal.getName());//TODO error check
        Chat chat = chatService.findChatById(Long.parseLong(id));

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(message);
        chatMessage.setAuthor(user);
        chatMessage.setChat(chat);

        chatMessage = chatMessageService.createNewMessage(chatMessage);
        chatService.addMessageToChat(chat, chatMessage);

        for(var chatUser: chat.getUsers()) {
            if(!chatUser.equals(user)) {
                Notification messageNotification = new Notification();
                messageNotification.setMessage("You have a new message in " + chat.getName());
                messageNotification.setFrom(chat);
                messageNotification.setTo(chatUser);
                messageNotification.setType(NotificationType.CHAT_MESSAGE);

                messageNotification = notificationService.createNotification(messageNotification);
                userService.sendNotification(messageNotification);
            }
        }

        return new ResponseEntity<>(new ApiResponse("You sent message in chat " + chat.getName()), HttpStatus.OK);
    }

}
