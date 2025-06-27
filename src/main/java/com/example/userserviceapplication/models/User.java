package com.example.userserviceapplication.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity(name = "users")
public class User extends BaseModel {
    private String username;
    private String email;
    private String password;
    @ManyToMany
    private List<Role> roleList;//to know does this user have admin role (or) ta role (or) mentor role (or) mentee role (or) not
}

/*
User : Role
1  :  m
m  :  1
------------
m  :  m
------------
 */