package com.example.userserviceapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

//created_at and updated_at are auditory fields. Enable them using @EnableJpaAuditing - keep trace of what is happening/audit details for company (i.e) your company had these many users and this day you company had these many users
@EnableJpaAuditing
@SpringBootApplication
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

}
