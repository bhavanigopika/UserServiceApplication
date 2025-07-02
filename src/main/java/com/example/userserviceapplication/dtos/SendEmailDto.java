package com.example.userserviceapplication.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendEmailDto {
    //attributes to send email
    private String subject;
    private String body;
    private String email;
}
