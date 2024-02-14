package com.treat.tinderdog.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.treat.tinderdog.data.UserLike;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.List;

public class UserLikesTest {

    @Test
    void testSerialiseDeserialize() throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        final String expectedJson =
                "{\"likes\":[{\"id\":null,\"userId\":10,\"likedUserId\":8,\"createdAt\":1704276000000}]}";
        final Timestamp time = Timestamp.valueOf("2024-01-03 10:00:00");
        final UserLike userLike = new UserLike();
        userLike.setUserId(10L);
        userLike.setLikedUserId(8L);
        userLike.setCreatedAt(time);
        final UserLikes likes = new UserLikes(List.of(userLike));

        assertThat(mapper.writeValueAsString(likes), is(expectedJson));
        assertThat(mapper.readValue(expectedJson, UserLikes.class), is(likes));
    }
}
