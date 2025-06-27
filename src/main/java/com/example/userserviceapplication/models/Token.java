package com.example.userserviceapplication.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name = "tokens")
public class Token extends BaseModel {
    private String tokenValue;
    //which user does the token belongs to
    @ManyToOne
    private User user;
    //LocalDateTime is not good idea to use if you use this project worldwide then some country have different time zone
    private LocalDateTime expiryDateTime;
}

/*
Token  : User
1 : 1
m : 1 -> 1 user have multiple token. How? See below
-------------
m : 1
-------------

Question - does Auth server maintain multiple tokens for different applications for one single user? YES,
one user have multiple tokens.

Eg., For scaler website we can see 2 tokens - trying to login to match 2 devices

Question - will there be different tokens for different applications or its just one token which will be
circulated among all the apps the user is trying to give access to?

One authorization have 1 token
In eg., CRED  you are using itn your browser and phone. For that 2 different tokens will be maintained
One token saved in mobile aplication (phone) and one token saved in browser

But when you login, token has generated then when you will use it then the token will be use in multiple
times till expires. When it expires, there is a concept of Refresh token

*/
