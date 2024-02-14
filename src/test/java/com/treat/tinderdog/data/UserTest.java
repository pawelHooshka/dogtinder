package com.treat.tinderdog.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class UserTest {

    @Test
    void testSerialiseDeserialize() throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        final String expectedJson =
                "{\"id\":1,\"username\":\"user\",\"password\":\"pwd\",\"currentProfilesPageNumber\":1}";
        final User user = new User();
        user.setId(1L);
        user.setUsername("user");
        user.setPassword("pwd");
        user.setCurrentProfilesPageNumber(1);

        assertThat(mapper.writeValueAsString(user), is(expectedJson));
        assertThat(mapper.readValue(expectedJson, User.class), is(user));
    }
}
