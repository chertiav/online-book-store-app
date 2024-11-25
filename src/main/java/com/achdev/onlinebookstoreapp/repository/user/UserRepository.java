package com.achdev.onlinebookstoreapp.repository.user;

import com.achdev.onlinebookstoreapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
}
