package com.treat.tinderdog.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.treat.tinderdog.data.UserMatch;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.List;

public class UserMatchesTest {

    @Test
    void testSerialiseDeserialize() throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        final String expectedJson =
                "{\"matches\":[{\"id\":null,\"firstUserId\":10,\"secondUserId\":8,\"createdAt\":1704276000000}]}";
        final Timestamp time = Timestamp.valueOf("2024-01-03 10:00:00");
        final UserMatch userMatch = new UserMatch();
        userMatch.setFirstUserId(10L);
        userMatch.setSecondUserId(8L);
        userMatch.setCreatedAt(time);
        final UserMatches matches = new UserMatches(List.of(userMatch));

        assertThat(mapper.writeValueAsString(matches), is(expectedJson));
        assertThat(mapper.readValue(expectedJson, UserMatches.class), is(matches));
    }
}
