package com.treat.tinderdog.data.repository;

import com.treat.tinderdog.data.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class UserRepositorySpringBootTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testUserRepository() {
        final User user = new User();
        user.setId(1L);
        user.setUsername("user");
        user.setPassword("pwd");
        user.setCurrentProfilesPageNumber(1);
        final User savedUser = userRepository.save(user);
        final Optional<User> retrievedUser = userRepository.findUserByUsername("user");
        assertTrue(retrievedUser.isPresent());
        assertThat(retrievedUser.get(), is(user));
        assertThat(retrievedUser.get(), is(savedUser));
    }
}
