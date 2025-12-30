package com.shivam.smartnotes.service;


import com.shivam.smartnotes.dto.UserRegisterRequest;
import com.shivam.smartnotes.dto.UserResponse;
import com.shivam.smartnotes.entity.User;

public interface UserService {
    UserResponse registerUser(UserRegisterRequest request);
    UserResponse getUserById(Long userid);
    UserResponse getUserByUsername(String username);
    User getUserEntityById(Long userId);
}
