package com.shivam.smartnotes.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {

    private Long userid;
    private String email;
    private String username;
}
