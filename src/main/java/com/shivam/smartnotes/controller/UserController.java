package com.shivam.smartnotes.controller;

import com.shivam.smartnotes.dto.UserRegisterRequest;
import com.shivam.smartnotes.dto.UserResponse;
import com.shivam.smartnotes.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


//Response entity gives:
//1)Status code (200, 201, 400, 404, 500…)
//2)Headers
//3)Response body
//get,post,put,delete
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService=userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(
            @RequestBody UserRegisterRequest request) {

        UserResponse response=userService.registerUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id){

        UserResponse response=userService.getUserById(id);

        return ResponseEntity
                .ok(response);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(
            @PathVariable String username){
        UserResponse response=userService.getUserByUsername(username);

        return ResponseEntity
                .ok(response);
    }

}
