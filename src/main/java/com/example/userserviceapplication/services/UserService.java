package com.example.userserviceapplication.services;

import com.example.userserviceapplication.exceptions.InvalidPasswordException;
import com.example.userserviceapplication.models.Token;
import com.example.userserviceapplication.models.User;
import org.springframework.stereotype.Service;


public interface UserService {
    public Token login(String email, String password) throws InvalidPasswordException;//Once login successful, return the Token
    public User signUp(String username, String email, String password);
    public void logout(String tokenValue);
    //To validate the token, user needs to send the token value
    public User validateToken(String tokenValue);
}
