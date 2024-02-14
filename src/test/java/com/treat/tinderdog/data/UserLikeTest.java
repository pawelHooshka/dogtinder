package com.treat.tinderdog.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

public class UserLikeTest {

    @Test
    void testSerialiseDeserialize() throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        final String expectedJson =
                "{\"id\":null,\"userId\":10,\"likedUserId\":8,\"createdAt\":1704276000000}";
        final Timestamp time = Timestamp.valueOf("2024-01-03 10:00:00");
        final UserLike userLike = new UserLike();
        userLike.setUserId(10L);
        userLike.setLikedUserId(8L);
        userLike.setCreatedAt(time);

        assertThat(mapper.writeValueAsString(userLike), is(expectedJson));
        assertThat(mapper.readValue(expectedJson, UserLike.class), is(userLike));
    }
}
