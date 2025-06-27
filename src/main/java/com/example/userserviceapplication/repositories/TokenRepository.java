package com.example.userserviceapplication.repositories;

import com.example.userserviceapplication.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    /*@Override
    <S extends Token> S save(S entity);*/
    Token save(Token token);
    /*
    We write query as
          select * from tokens where tokenValue = tokenValue and expiryDateTime > localDateTime;
    tokenValue on left side represents in Token class
    tokenValue on right side represents in 23rd line
    expiryDateTime represents in Token class
    localDateTime is currentTime -> represents query in 23rd line
     */
    Optional<Token> findByTokenValueAndExpiryDateTimeGreaterThan(String tokenValue, LocalDateTime expiryDateTimeIsGreaterThan);
    Optional<Token> findByTokenValue(String tokenValue);

}
