package com.example.userserviceapplication.models;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//Why can't we use Role as enum instead of class because may someone add more roles in the future. In enum, once we entered
//we have to go to class and add role but during running the application I can add roles. So, class is better than enum
//Enum is good for constants Eg., country code (They are not going to change only limited number of code)
@Entity(name = "roles")
public class Role extends BaseModel {
    //roleName (or) value - admin, mentor, teachingAssistant, mentee, student
    private String value;
}
