package com.example.usersapi.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.usersapi.model.User;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Get users in birth dates range")
    @Sql(scripts = "classpath:database/add-five-users.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/clear-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByBirthDateBetween_ValidRequest_ShouldReturnListOfUsers() {
        LocalDate from = LocalDate.of(1985, 2, 2);
        LocalDate to = LocalDate.of(1991, 1, 1);
        List<User> actual = userRepository.findByBirthDateBetween(from, to);
        Assertions.assertEquals(3, actual.size());
        for (User user : actual) {
            assertThat(user.getBirthDate().getYear()).isIn(1985, 1988, 1990);
        }

        to = LocalDate.of(2000, 1, 1);
        actual = userRepository.findByBirthDateBetween(from, to);
        Assertions.assertEquals(5, actual.size());
        for (User user : actual) {
            assertThat(user.getBirthDate().getYear()).isIn(1985, 1988, 1990, 1992, 1995);
        }

        from = LocalDate.of(1992, 11, 28);
        to = LocalDate.of(1995, 3, 10);
        actual = userRepository.findByBirthDateBetween(from, to);
        Assertions.assertEquals(2, actual.size());
        for (User user : actual) {
            assertThat(user.getBirthDate().getYear()).isIn(1992, 1995);
        }
    }
}
