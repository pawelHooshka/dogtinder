package com.treat.tinderdog.service;

import static com.treat.tinderdog.TestUtils.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.google.common.collect.Maps;
import com.treat.tinderdog.data.CustomUserDetails;
import com.treat.tinderdog.data.User;
import com.treat.tinderdog.data.UserLike;
import com.treat.tinderdog.data.UserMatch;
import com.treat.tinderdog.data.repository.UserRepository;
import com.treat.tinderdog.model.Animal;
import com.treat.tinderdog.model.AnimalsList200Response;
import com.treat.tinderdog.petfinder.client.Oauth2PetFinderRestClient;
import com.treat.tinderdog.service.exception.ProfileNotFoundException;
import com.treat.tinderdog.service.exception.UserNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class PetFinderServiceTest {

    private static final long USER_ID = 10L;
    private static final UUID MATCH_ID = UUID.fromString("916d8805-9192-4323-b0f2-698ade4a6399");
    private static final UUID LIKE_ID = UUID.fromString("27e576f7-454a-4783-b652-4c467050285b");
    @InjectMocks
    private PetFinderService petFinderService;
    @Mock
    private Oauth2PetFinderRestClient petFinderRestTemplate;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;

    private User user;
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        this.user = user(USER_ID, "user", "password", 1);
        this.userDetails = mock(CustomUserDetails.class);
    }

    @Test
    void testGetDogs() {
        when(userDetails.getId()).thenReturn(USER_ID);
        when(userRepository.findById(eq(USER_ID))).thenReturn(Optional.of(user));
        final Map<String, Object> queryParameters = Maps.newHashMap();
        queryParameters.put("name", "some name");

        final Map<String, Object> expectedQueryParameters = Maps.newHashMap();
        expectedQueryParameters.put("name", "some name");
        expectedQueryParameters.put("page", 1);

        final AnimalsList200Response dogs = animals(2, "10");
        when(petFinderRestTemplate
                .get(anyString(), eq(expectedQueryParameters), eq(AnimalsList200Response.class)))
                .thenReturn(ResponseEntity.ok(dogs));

        final AnimalsList200Response animals = petFinderService.getDogs(userDetails, queryParameters);
        assertThat(animals, is(dogs));
        assertThat(queryParameters, is(expectedQueryParameters));
        assertEquals(user.getCurrentProfilesPageNumber(), 2);
        verify(userRepository).save(eq(user));
    }

    @Test
    void testGetDogsOwnUserRecordIsRemoved() {
        when(userDetails.getId()).thenReturn(USER_ID);
        when(userRepository.findById(eq(USER_ID))).thenReturn(Optional.of(user));
        final Map<String, Object> queryParameters = Maps.newHashMap();
        queryParameters.put("name", "some name");

        final Map<String, Object> expectedQueryParameters = Maps.newHashMap();
        expectedQueryParameters.put("name", "some name");
        expectedQueryParameters.put("page", 1);

        final AnimalsList200Response dogs = animals(10, "10");
        final AnimalsList200Response expectedAnimals = animals();

        when(petFinderRestTemplate
                .get(anyString(), eq(expectedQueryParameters), eq(AnimalsList200Response.class)))
                .thenReturn(ResponseEntity.ok(dogs));

        final AnimalsList200Response animals = petFinderService.getDogs(userDetails, queryParameters);

        assertThat(animals, is(expectedAnimals));
        assertThat(queryParameters, is(expectedQueryParameters));
        assertEquals(user.getCurrentProfilesPageNumber(), 2);
        verify(userRepository).save(eq(user));
    }

    @Test
    void testGetDogsUserDoesntExist() {
        when(userDetails.getId()).thenReturn(USER_ID);
        when(userRepository.findById(eq(USER_ID))).thenReturn(Optional.empty());
        final Map<String, Object> queryParameters = Maps.newHashMap();
        queryParameters.put("name", "some name");

        final UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            petFinderService.getDogs(userDetails, queryParameters);
        });
        assertNotNull(exception);
        assertFalse(queryParameters.containsKey("page"));
        assertEquals(user.getCurrentProfilesPageNumber(), 1);
        verify(userRepository, never()).save(any());
        verify(petFinderRestTemplate, never()).get(anyString(), any(), any());
    }

    @Test
    void testGetMatchedProfile() {
        when(userDetails.getId()).thenReturn(USER_ID);
        final UserMatch userMatch = userMatch(USER_ID, MATCH_ID);
        final Animal animal = animal(1, "10");
        when(userService.getUserMatch(userDetails, MATCH_ID)).thenReturn(userMatch);
        when(petFinderRestTemplate.get(eq("/animals/1"), eq(Animal.class)))
                .thenReturn(ResponseEntity.ok(animal));
        final Animal actualAnimal = petFinderService.getMatchedProfile(userDetails, MATCH_ID);
        assertThat(actualAnimal, is(animal));
        verify(petFinderRestTemplate).get(eq("/animals/1"), eq(Animal.class));
    }

    @Test
    void testGetMatchedProfileNotFound() {
        when(userDetails.getId()).thenReturn(USER_ID);
        final UserMatch userMatch = userMatch(USER_ID, MATCH_ID);
        when(userService.getUserMatch(userDetails, MATCH_ID)).thenReturn(userMatch);
        when(petFinderRestTemplate.get(eq("/animals/1"), eq(Animal.class)))
                .thenReturn(ResponseEntity.ok().build());

        final ProfileNotFoundException exception = assertThrows(
                ProfileNotFoundException.class, () -> petFinderService.getMatchedProfile(userDetails, MATCH_ID));

        assertNotNull(exception);
        verify(petFinderRestTemplate).get(eq("/animals/1"), eq(Animal.class));
    }

    @Test
    void testGetLikedProfile() {
        final UserLike userLike = userLike(USER_ID, LIKE_ID);
        final Animal animal = animal(1, "10");
        when(userService.getUserLike(userDetails, LIKE_ID)).thenReturn(userLike);
        when(petFinderRestTemplate.get(eq("/animals/1"), eq(Animal.class)))
                .thenReturn(ResponseEntity.ok(animal));
        final Animal actualAnimal = petFinderService.getLikedProfile(userDetails, LIKE_ID);
        assertThat(actualAnimal, is(animal));
        verify(petFinderRestTemplate).get(eq("/animals/1"), eq(Animal.class));
    }

    @Test
    void testGetLikedProfileNotFound() {
        final UserLike userLike = userLike(USER_ID, LIKE_ID);
        when(userService.getUserLike(userDetails, LIKE_ID)).thenReturn(userLike);
        when(petFinderRestTemplate.get(eq("/animals/1"), eq(Animal.class)))
                .thenReturn(ResponseEntity.ok().build());

        final ProfileNotFoundException exception = assertThrows(
                ProfileNotFoundException.class, () -> petFinderService.getLikedProfile(userDetails, LIKE_ID));

        assertNotNull(exception);
        verify(petFinderRestTemplate).get(eq("/animals/1"), eq(Animal.class));
    }
}
