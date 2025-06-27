package com.example.userserviceapplication.configs;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Configuration
//Make it as configuration file. So, spring boot knows that it needs to scan the file whenever start the application
public class AppConfig {
    @Bean//once bean created and it store it in the application context
    public BCryptPasswordEncoder getBCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
