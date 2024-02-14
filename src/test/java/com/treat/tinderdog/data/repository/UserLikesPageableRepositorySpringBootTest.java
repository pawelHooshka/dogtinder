package com.treat.tinderdog.data.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.treat.tinderdog.data.UserLike;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

@DataJpaTest
public class UserLikesPageableRepositorySpringBootTest {

    @Autowired
    private UserLikesPageableRepository userLikesPageableRepository;

    @Autowired
    private UserLikesRepository userLikesRepository;

    @Test
    void testUserLikesPageableRepository() {
        final UserLike userLike = new UserLike();
        userLike.setUserId(10L);
        userLike.setLikedUserId(8L);

        final UserLike savedLike = userLikesRepository.save(userLike);
        assertNotNull(savedLike.getId());
        assertNotNull(savedLike.getCreatedAt());

        final Optional<List<UserLike>> retrievedLike =
                userLikesPageableRepository
                        .findAllByUserId(10L, PageRequest.of(0, 1));
        assertTrue(retrievedLike.isPresent());
        assertThat(retrievedLike.get().get(0), is(savedLike));
    }
}
