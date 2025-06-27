package com.example.userserviceapplication.repositories;

import com.example.userserviceapplication.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository//Mark this otherwise bean is not created
public interface UserRepository extends JpaRepository<User, Long> {
    User save(User user);
    Optional<User> findByEmail(String email);
}
