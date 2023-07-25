package com.bfs.authenticationservice.controller;

import com.bfs.authenticationservice.domain.User;
import com.bfs.authenticationservice.dto.requests.LoginRequest;
import com.bfs.authenticationservice.dto.responses.LoginResponse;
import com.bfs.authenticationservice.exception.InvalidCredentialsException;
import com.bfs.authenticationservice.security.AuthUserDetail;
import com.bfs.authenticationservice.security.JwtProvider;
import com.bfs.authenticationservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
public class LoginController {

    private AuthenticationManager authenticationManager;
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    private UserService userService;
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    private JwtProvider jwtProvider;
    @Autowired
    public void setJwtProvider(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    //User trying to log in with username and password
    @PostMapping("/auth/login")
    @ResponseBody
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, BindingResult result) throws InvalidCredentialsException {

        Authentication authentication;
        LoginResponse loginResponse;

        if (result.hasErrors()) {
            String message = result.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            loginResponse = LoginResponse.builder()
                    .message(message)
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(loginResponse);
        }

        //Try to authenticate the user using the username and password
        try{
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (AuthenticationException e){
            throw new InvalidCredentialsException("Incorrect credentials, please try again.");
        }

        //Successfully authenticated user will be stored in the authUserDetail object
        AuthUserDetail authUserDetail = (AuthUserDetail) authentication.getPrincipal(); //getPrincipal() returns the user object

        //A token wil be created using the username/email/userId and permission
        String token = jwtProvider.createToken(authUserDetail);

        User user = userService.getUserById(authUserDetail.getId());

        if (!user.getActive() || user.getType() == 4) {
            loginResponse = LoginResponse.builder()
                    .message("User " + authUserDetail.getUsername() + " is banned")
                    .build();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(loginResponse);
        }

        loginResponse = LoginResponse.builder()
                .message("User " + authUserDetail.getUsername() + " successfully logged in")
                .token(token)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(loginResponse);

    }

}
