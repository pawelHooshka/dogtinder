package com.treat.tinderdog.data.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.treat.tinderdog.data.UserMatch;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

@DataJpaTest
public class UserMatchesRepositorySpringBootTest {

    @Autowired
    private UserMatchesRepository userMatchesRepository;

    @Test
    void testFindAllByFirstUserIdAndSecondUserId() {
        final UserMatch userMatch = new UserMatch();
        userMatch.setFirstUserId(10L);
        userMatch.setSecondUserId(8L);
        final UserMatch savedMatch = userMatchesRepository.save(userMatch);

        assertNotNull(savedMatch);
        assertNotNull(savedMatch.getId());
        assertNotNull(savedMatch.getCreatedAt());
        assertNotNull(userMatch.getCreatedAt());

        final Optional<UserMatch> retrievedUserMatch =
                userMatchesRepository.findAllByFirstUserIdAndSecondUserId(10L, 8L);
        assertTrue(retrievedUserMatch.isPresent());
        assertThat(retrievedUserMatch.get(), is(savedMatch));
    }

    @Test
    void testFindAllByFirstUserIdOrSecondUserId() {
        final UserMatch userMatch = new UserMatch();
        userMatch.setFirstUserId(10L);
        userMatch.setSecondUserId(8L);
        final UserMatch savedMatch = userMatchesRepository.save(userMatch);

        assertNotNull(savedMatch);
        assertNotNull(savedMatch.getId());
        assertNotNull(savedMatch.getCreatedAt());
        assertNotNull(userMatch.getCreatedAt());

        final Optional<List<UserMatch>> retrievedUserMatch =
                userMatchesRepository.findAllByFirstUserIdOrSecondUserId(10L, 1L);
        assertThat(retrievedUserMatch.get().get(0), is(savedMatch));

        final Optional<List<UserMatch>> secondRetrievedUserMatch =
                userMatchesRepository.findAllByFirstUserIdOrSecondUserId(1L, 8L);
        assertThat(secondRetrievedUserMatch.get().get(0), is(savedMatch));
    }

    @Test
    void testFindAllByFirstUserIdOrSecondUserIdReturnsEmptyWhenNoIdMatches() {
        final UserMatch userMatch = new UserMatch();
        userMatch.setFirstUserId(10L);
        userMatch.setSecondUserId(8L);
        final UserMatch savedMatch = userMatchesRepository.save(userMatch);

        assertNotNull(savedMatch);
        assertNotNull(savedMatch.getId());
        assertNotNull(savedMatch.getCreatedAt());
        assertNotNull(userMatch.getCreatedAt());

        assertTrue(userMatchesRepository.findAllByFirstUserIdOrSecondUserId(1L, 2L).get().isEmpty());
    }
}
