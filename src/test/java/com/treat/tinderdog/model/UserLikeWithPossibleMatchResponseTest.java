package com.treat.tinderdog.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.UUID;

public class UserLikeWithPossibleMatchResponseTest {

    @Test
    void testSerialiseDeserialize() throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        final Timestamp time = Timestamp.valueOf("2024-01-03 10:00:00");
        final UserLikeWithPossibleMatchResponse response =
                new UserLikeWithPossibleMatchResponse(
                        10L,
                        8L,
                        UUID.fromString("9586d901-2e7b-4f49-8cb2-c67fb9a0dcb4"),
                        UUID.fromString("9586d901-2e7b-4f49-8cb2-c67fb9a0dcb4"),
                        time,
                        time);
        final String expectedJson =
                "{\"userId\":10,\"likedUserId\":8,\"likeId\":\"9586d901-2e7b-4f49-8cb2-c67fb9a0dcb4\"," +
                        "\"matchId\":\"9586d901-2e7b-4f49-8cb2-c67fb9a0dcb4\",\"likeCreatedAt\":1704276000000," +
                        "\"matchCreatedAt\":1704276000000}";
        assertThat(mapper.writeValueAsString(response), is(expectedJson));
        assertThat(mapper.readValue(expectedJson, UserLikeWithPossibleMatchResponse.class), is(response));
    }
}
