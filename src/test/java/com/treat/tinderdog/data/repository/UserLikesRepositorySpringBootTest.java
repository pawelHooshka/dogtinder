package com.treat.tinderdog.data.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.treat.tinderdog.data.UserLike;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

@DataJpaTest
public class UserLikesRepositorySpringBootTest {

    @Autowired
    private UserLikesRepository userLikesRepository;

    @Test
    void testUserLikesRepository() {
        final UserLike userLike = new UserLike();
        userLike.setUserId(10L);
        userLike.setLikedUserId(8L);
        final UserLike savedLike = userLikesRepository.save(userLike);

        assertNotNull(savedLike);
        assertNotNull(savedLike.getId());
        assertNotNull(savedLike.getCreatedAt());
        assertNotNull(userLike.getCreatedAt());

        final Optional<List<UserLike>> retrievedUserLike = userLikesRepository.findAllByUserId(10L);
        assertTrue(retrievedUserLike.isPresent());
        assertThat(retrievedUserLike.get().get(0), is(savedLike));

        final Optional<UserLike> secondRetrievedUserLike =
                userLikesRepository.findByUserIdAndLikedUserId(10L, 8L);
        assertThat(retrievedUserLike.get().get(0), is(secondRetrievedUserLike.get()));
    }
}
