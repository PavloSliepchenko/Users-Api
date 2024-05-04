package com.example.usersapi.repository;

import com.example.usersapi.model.User;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByBirthDateBetween(LocalDate fromDate, LocalDate toDate);
}
