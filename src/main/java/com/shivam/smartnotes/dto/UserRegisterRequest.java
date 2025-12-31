package com.shivam.smartnotes.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


@Data
public class UserRegisterRequest {

    @NotBlank(message="Username cannot be blank")
    private String username;

    @Email(message = "Enter must be valid")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    private String password;
}
