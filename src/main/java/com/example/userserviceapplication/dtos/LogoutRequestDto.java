package com.example.userserviceapplication.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogoutRequestDto {
    //Going to log out, so token needs as it invalidates the token - If I give a token then it invalidate the token or session close
    private String tokenValue;
}
