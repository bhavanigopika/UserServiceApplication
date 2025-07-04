package com.example.userserviceapplication.services;

import com.example.userserviceapplication.exceptions.InvalidPasswordException;
import com.example.userserviceapplication.models.Token;
import com.example.userserviceapplication.models.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;


public interface UserService {
    //For JWT, signUp method is enough
    //public Token login(String email, String password) throws InvalidPasswordException;//Once login successful, return the Token

    User signUp(String username, String email, String password) throws JsonProcessingException;

    //public void logout(String tokenValue);

    //To validate the token, user needs to send the token value
    //public User validateToken(String tokenValue);

    //let's authenticate the user
    User authenticateUser(String email, String password);
    User getUserByEmail(String email);
}
