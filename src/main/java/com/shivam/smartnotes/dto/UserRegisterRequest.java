package com.shivam.smartnotes.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


@Data
public class UserRegisterRequest {


    private String username;
    private String email;
    private String password;
}
