package com.bfs.authenticationservice.AOP;

import com.bfs.authenticationservice.dto.responses.LoginResponse;
import com.bfs.authenticationservice.exception.InvalidCredentialsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class MyExceptionHandler {

    @ExceptionHandler(value = {InvalidCredentialsException.class})
    public ResponseEntity<LoginResponse> handleInvalidCredentialsException(InvalidCredentialsException e){
        return new ResponseEntity(LoginResponse.builder().message(e.getMessage()).build(), HttpStatus.FORBIDDEN);
    }

}
