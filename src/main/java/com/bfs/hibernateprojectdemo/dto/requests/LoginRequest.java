package com.bfs.hibernateprojectdemo.dto.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class LoginRequest {
    @NotBlank(message = "username cannot be blank")
    @Size(max = 255, message = "name must have fewer then 255 characters")
    private String username;

    @NotBlank(message = "password cannot be blank")
    @Size(max = 255, message = "password must have fewer then 255 characters")
    private String password;
}
