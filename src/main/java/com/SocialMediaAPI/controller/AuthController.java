package com.SocialMediaAPI.controller;

import com.SocialMediaAPI.dto.*;
import com.SocialMediaAPI.exception.UserLoginException;
import com.SocialMediaAPI.model.CustomUserDetails;
import com.SocialMediaAPI.model.User;
import com.SocialMediaAPI.security.JwtTokenProvider;
import com.SocialMediaAPI.service.AuthService;
import com.SocialMediaAPI.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Operation(summary = "Register new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registered new user",
                    content = { @Content(mediaType = "text/plain",
                            examples = @ExampleObject("User 'username' successfully created!")) }),
            @ApiResponse(responseCode = "400", description = "Invalid user input, param UserAuthDto is not valid",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDto.class)) }),
            @ApiResponse(responseCode = "409", description = "User already registered",
                    content = { @Content(mediaType = "text/plain",
                            examples = @ExampleObject("User with username: 'username' is already exist!")) }),
            @ApiResponse(responseCode = "401", description = "Un-Authorized user", content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })

    @PostMapping("/registration")
    public ResponseEntity<?> register(@Valid @RequestBody(required = false) UserAuthDto userAuthDto){
        if(userAuthDto == null)
            return new ResponseEntity<>(new ApiErrorDto(HttpStatus.BAD_REQUEST, "/api/auth/registration", NullPointerException.class.getName(), "Required body: 'UserAuthDto' is missing!"), HttpStatus.BAD_REQUEST);

        User user = new User();
        user.setUsername(userAuthDto.getUsername());
        user.setPassword(userAuthDto.getPassword());
        user.setEmail(userAuthDto.getEmail());
        userService.registerNewUserAccount(user);
        return new ResponseEntity<>(new ApiResponseSingleOk("Registration", "User '" + user.getUsername() + "' successfully created!"), HttpStatus.OK);
    }

    @Operation(summary = "Login and Get user token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get user token",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TokenDto.class)) }),
            @ApiResponse(responseCode = "401", description = "Bad credentials username or password",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDto.class)) }),
            @ApiResponse(responseCode = "401", description = "Un-Authorized user", content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginRequest){
        Authentication authentication = authService.authenticateUser(loginRequest)
                .orElseThrow(() -> new UserLoginException("Couldn't login user [" + loginRequest + "]"));

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        System.out.println("Logged in User returned [API]: " + customUserDetails.getUsername());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwtToken = authService.generateToken(customUserDetails);
        TokenDto token = new TokenDto();
        token.setToken(jwtToken);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }
}
