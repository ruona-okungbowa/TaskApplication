package com.example.todo.service;

import com.example.todo.model.User;
import com.example.todo.repository.UserRepository;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User addUser(@NotNull(message = "User cannot be null") User user){
        if (userRepository.findByUsername(user.getUsername()).isPresent()){
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()){
            throw new RuntimeException("Email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles("ROLE_USER");
        return userRepository.save(user);
    }

    // get a user by username
    public User getUserByUsername(@NotNull(message = "Username cannot be null") @NotBlank(message = "Username cannot be blank") String username){
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
    }

    // get a user by email
    public User getUserByEmail(
            @NotNull(message = "Email cannot be null")
            @NotBlank(message = "Email cannot be blank")
            @Email(message = "Email should be vaild")
            String email
    ){
        return userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RuntimeException("User not found")
                );
    }

    // get a user by id
    public User getUserById(
            @NotNull(message = "Id cannot be null")
            Long id
            ){
        return userRepository.findById(id)
                .orElseThrow(
                        () -> new RuntimeException("User not found")
                );
    }

    // update a user
    public User updateUser(
            @NotNull(message = "User cannot be null")
            User user
    ){
        if (userRepository.findByUsername(user.getUsername()).isEmpty()){
            throw new RuntimeException("User not found");
        }
        return userRepository.save(user);
    }

    // delete a user
    public void deleteUser(
            @NotNull(message = "User cannot be null")
            User user
    ){
        if (userRepository.findByUsername(user.getUsername()).isEmpty()){
            throw new RuntimeException("User not found");
        }
        userRepository.delete(user);
    }

    // get all users
    public List<User> getAllUsers(){
        if (userRepository.findAll().isEmpty()){
            throw new RuntimeException("No users found");
        }
        return userRepository.findAll();
    }



}
