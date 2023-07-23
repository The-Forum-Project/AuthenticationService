package com.bfs.authenticationservice.controller;

import com.bfs.authenticationservice.domain.User;
import com.bfs.authenticationservice.dto.requests.SignupRequest;
import com.bfs.authenticationservice.dto.responses.MessageResponse;
import com.bfs.authenticationservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
public class SignupController {
    private final UserService userService;

    public SignupController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/auth/signup")
    @ResponseBody
    public ResponseEntity<MessageResponse> addUser(@Valid @RequestBody SignupRequest request, BindingResult result) {

        MessageResponse messageResponse;

        if (result.hasErrors()) {
            String message = result.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            messageResponse = MessageResponse.builder()
                    .message(message)
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageResponse);
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(request.getPassword())
                .active(true)
                .dateJoined(LocalDateTime.now())
                .type(4)
                .code("000000")
                .profileImageURL("https://forumproject.s3.us-east-2.amazonaws.com/default.jpg")
                .build();

        Boolean addSuccess = userService.addUser(user);
        String message;

        if (!addSuccess) {
            message = "Duplicate username or email";
            messageResponse = MessageResponse.builder()
                    .message(message)
                    .build();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(messageResponse);

        } else {
            message = "Success! User was added";
            messageResponse = MessageResponse.builder()
                    .message(message)
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
        }

    }

}
