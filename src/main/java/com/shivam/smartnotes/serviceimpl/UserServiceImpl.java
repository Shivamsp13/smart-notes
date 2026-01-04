package com.shivam.smartnotes.serviceimpl;

import com.shivam.smartnotes.dto.UserRegisterRequest;
import com.shivam.smartnotes.dto.UserResponse;
import com.shivam.smartnotes.entity.User;
import com.shivam.smartnotes.exceptions.UserAlreadyExistException;
import com.shivam.smartnotes.exceptions.UserNotFoundException;
import com.shivam.smartnotes.repository.UserRepository;
import com.shivam.smartnotes.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository){
        this.userRepository=userRepository;
    }

    @Override
    public UserResponse registerUser(UserRegisterRequest request){

        if(userRepository.existsByUsername(request.getUsername())){
            throw new UserAlreadyExistException("Username Already Exists"+request.getUsername());
        }
        if(userRepository.existsByEmail(request.getEmail())){
            throw new UserAlreadyExistException("Email Already Exists"+request.getEmail());
        }

        User user=User.builder().email(request.getEmail())
                .username(request.getUsername())
                .password(request.getPassword())
                .build();

        User savedUser=userRepository.save(user);
         return mapToResponse(user);
    }


    @Override
    public UserResponse getUserById(Long userid) {
        User user = userRepository.findById(userid)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with id: " + userid));

        return mapToResponse(user);
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        User user=userRepository.findByUsername(username)
                .orElseThrow(()->
                        new UserNotFoundException("User not found with username"+username)
                );
        return mapToResponse(user);
    }

    @Override
    public User getUserEntityById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(()->new UserNotFoundException("User not found with id"+userId)
                );
    }

    public UserResponse mapToResponse(User user){
        return new UserResponse(
                user.getUserId(),
                user.getEmail(),
                user.getUsername()
        );
    }
}
