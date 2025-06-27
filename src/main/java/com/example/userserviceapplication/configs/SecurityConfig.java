package com.example.userserviceapplication.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

/*    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // Disable CSRF for stateless APIs
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/signup").permitAll()  // Allow unauthenticated POST to /signup
                .anyRequest().authenticated()  // Secure other endpoints which require authentication
            )
            .httpBasic(Customizer.withDefaults());  // Enable HTTP Basic auth or other mechanisms
        return http.build();
    }*/
/*    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/signup").permitAll()  // Allow unauthenticated POST to /signup
                .anyRequest().authenticated()  // Secure other endpoints which require authentication
            )
             .csrf(csrf -> csrf.disable());  // Disable CSRF for stateless APIs
        return http.build();
    }*/

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> {
                        try{
                            requests
                                .anyRequest().permitAll()
                                .and().cors().disable()
                                .csrf().disable();
                        }catch(Exception e){
                            throw new RuntimeException(e);
                        }
                    }
                );
        return http.build();
    }
}
