package com.example.userserviceapplication.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDto {
    //once login, the response would be token
    private String tokenValue;
    //In response, we won't see password, otherwise easy to hack
    //private String password;
    private ResponseStatus responseStatus;
}
