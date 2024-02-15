package com.treat.tinderdog.service;

import static com.treat.tinderdog.TestUtils.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.treat.tinderdog.data.CustomUserDetails;
import com.treat.tinderdog.data.User;
import com.treat.tinderdog.data.UserLike;
import com.treat.tinderdog.data.UserMatch;
import com.treat.tinderdog.data.repository.UserLikesPageableRepository;
import com.treat.tinderdog.data.repository.UserLikesRepository;
import com.treat.tinderdog.data.repository.UserMatchesRepository;
import com.treat.tinderdog.data.repository.UserRepository;

import com.treat.tinderdog.model.UserLikeWithPossibleMatchResponse;
import com.treat.tinderdog.model.UserLikes;
import com.treat.tinderdog.model.UserMatches;
import com.treat.tinderdog.service.exception.LikeAlreadyExistsException;
import com.treat.tinderdog.service.exception.NotAuthorisedToAccessResourceException;
import com.treat.tinderdog.service.exception.UserLikeDoesNotExistException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private static final long USER_ID = 10L;

    private static final long OTHER_USER_ID = 5L;
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final UUID LIKE_ID = UUID.fromString("6dacda77-b365-4dca-880b-907d11ec9751");
    private static final UUID MATCH_ID = UUID.fromString("b10f648d-277b-4972-92c9-d1440393962b");

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserLikesRepository userLikesRepository;
    @Mock
    private UserLikesPageableRepository userLikesPageableRepository;
    @Mock
    private UserMatchesRepository userMatchesRepository;

    private User user;

    @BeforeEach
    void setUp() {
        this.user = user(USER_ID, USERNAME, PASSWORD, 1);
    }

    @Test
    void testLoadUserByUsername() {
        when(userRepository.findUserByUsername(eq(USERNAME))).thenReturn(Optional.of(user));
        final CustomUserDetails userDetails = new CustomUserDetails(user);
        assertThat(userService.loadUserByUsername(USERNAME), is(userDetails));
    }

    @Test
    void testLoadUserByUsernameUserNotFound() {
        when(userRepository.findUserByUsername(eq(USERNAME))).thenReturn(Optional.empty());
        final UsernameNotFoundException exception =
                assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(USERNAME));
        assertNotNull(exception);
    }

    @Test
    void testGetUserLikes() {
        final List<UserLike> likes = List.of(userLike(USER_ID, LIKE_ID));
        final CustomUserDetails userDetails = new CustomUserDetails(user(USER_ID, USERNAME, PASSWORD, 1));
        when(userLikesRepository.findAllByUserId(eq(USER_ID))).thenReturn(Optional.of(likes));
        final UserLikes userLikes = userService.getUserLikes(userDetails);
        assertThat(userLikes, is(new UserLikes(likes)));
    }

    @Test
    void testGetUserLikesUserLikesNotFound() {
        final CustomUserDetails userDetails = new CustomUserDetails(user(USER_ID, USERNAME, PASSWORD, 1));
        when(userLikesRepository.findAllByUserId(eq(USER_ID))).thenReturn(Optional.empty());
        final UserLikes userLikes = userService.getUserLikes(userDetails);
        assertThat(userLikes, is(new UserLikes(new ArrayList<>())));
    }

    @Test
    void testGetPagedUserLikes() {
        final List<UserLike> likes = List.of(userLike(USER_ID, LIKE_ID));
        final CustomUserDetails userDetails = new CustomUserDetails(user(USER_ID, USERNAME, PASSWORD, 1));
        when(userLikesPageableRepository.findAllByUserId(eq(USER_ID), any(PageRequest.class)))
                .thenReturn(Optional.of(likes));
        assertThat(userService.getUserLikes(userDetails,1, 2, null, null),
                is(new UserLikes(likes)));
        verify(userLikesPageableRepository).findAllByUserId(eq(USER_ID), eq(PageRequest.of(1, 2)));
    }

    @Test
    void testGetPagedUserLikesWithSortingAscending() {
        final List<UserLike> likes = List.of(userLike(USER_ID, LIKE_ID));
        final CustomUserDetails userDetails = new CustomUserDetails(user(USER_ID, USERNAME, PASSWORD, 1));
        when(userLikesPageableRepository.findAllByUserId(eq(USER_ID), any(PageRequest.class)))
                .thenReturn(Optional.of(likes));
        assertThat(userService.getUserLikes(userDetails,1, 2, "createdAt", "ASC"),
                is(new UserLikes(likes)));
        verify(userLikesPageableRepository).findAllByUserId(
                eq(USER_ID),
                eq(PageRequest.of(1, 2, Sort.by("createdAt").ascending())));
    }

    @Test
    void testGetUserMatches() {
        final List<UserMatch> matches = List.of(userMatch(USER_ID, MATCH_ID));
        final CustomUserDetails userDetails = new CustomUserDetails(user(USER_ID, USERNAME, PASSWORD, 1));
        when(userMatchesRepository.findAllByFirstUserIdOrSecondUserId(USER_ID, USER_ID))
                .thenReturn(Optional.of(matches));
        assertThat(userService.getUserMatches(userDetails), is(new UserMatches(matches)));
    }

    @Test
    void testGetUserMatchesMatchesNotFound() {
        final CustomUserDetails userDetails = new CustomUserDetails(user(USER_ID, USERNAME, PASSWORD, 1));
        when(userMatchesRepository.findAllByFirstUserIdOrSecondUserId(USER_ID, USER_ID))
                .thenReturn(Optional.empty());
        assertThat(userService.getUserMatches(userDetails), is(new UserMatches(new ArrayList<>())));
    }

    @Test
    void testGetUserMatch() {
        final UserMatch userMatch = userMatch(USER_ID, MATCH_ID);
        final CustomUserDetails userDetails = new CustomUserDetails(user(USER_ID, USERNAME, PASSWORD, 1));
        when(userMatchesRepository.findById(eq(MATCH_ID))).thenReturn(Optional.of(userMatch));
        assertThat(userService.getUserMatch(userDetails, MATCH_ID), is(userMatch));
    }

    @Test
    void testGetUserMatchMatchForbidden() {
        final UserMatch userMatch = userMatch(OTHER_USER_ID, MATCH_ID);
        final CustomUserDetails userDetails = new CustomUserDetails(user(USER_ID, USERNAME, PASSWORD, 1));
        when(userMatchesRepository.findById(eq(MATCH_ID))).thenReturn(Optional.of(userMatch));
        final NotAuthorisedToAccessResourceException exception =
                assertThrows(
                        NotAuthorisedToAccessResourceException.class,
                        () -> userService.getUserMatch(userDetails, MATCH_ID));
        assertNotNull(exception);
    }

    @Test
    void testGetUserLike() {
        final UserLike userLike = userLike(USER_ID, LIKE_ID);
        final CustomUserDetails userDetails = new CustomUserDetails(user(USER_ID, USERNAME, PASSWORD, 1));
        when(userLikesRepository.findById(eq(LIKE_ID))).thenReturn(Optional.of(userLike));
        assertThat(userService.getUserLike(userDetails, LIKE_ID), is(userLike));
    }

    @Test
    void testGetUserLikeLikeForbidden() {
        final UserLike userLike = userLike(OTHER_USER_ID, LIKE_ID);
        final CustomUserDetails userDetails = new CustomUserDetails(user(USER_ID, USERNAME, PASSWORD, 1));
        when(userLikesRepository.findById(eq(LIKE_ID))).thenReturn(Optional.of(userLike));
        final NotAuthorisedToAccessResourceException exception =
                assertThrows(
                        NotAuthorisedToAccessResourceException.class,
                        () -> userService.getUserLike(userDetails, LIKE_ID));
        assertNotNull(exception);
    }

    @Test
    void testLikeProfile() {
        final UserLikeWithPossibleMatchResponse response =
                new UserLikeWithPossibleMatchResponse(USER_ID, OTHER_USER_ID, LIKE_ID, MATCH_ID, null, null);
        final CustomUserDetails userDetails = new CustomUserDetails(user(USER_ID, USERNAME, PASSWORD, 1));
        when(userLikesRepository.findByUserIdAndLikedUserId(eq(USER_ID), eq(OTHER_USER_ID)))
                .thenReturn(Optional.empty());
        when(userLikesRepository.findByUserIdAndLikedUserId(eq(OTHER_USER_ID), eq(USER_ID)))
                .thenReturn(Optional.of(userLike(OTHER_USER_ID, USER_ID, LIKE_ID)));

        final UserLike savedUserLike = userLike(USER_ID, OTHER_USER_ID, LIKE_ID);
        final UserMatch savedUserMatch = userMatch(USER_ID, OTHER_USER_ID, MATCH_ID);

        when(userLikesRepository.save(any())).thenReturn(savedUserLike);
        when(userMatchesRepository.save(any())).thenReturn(savedUserMatch);
        assertThat(userService.likeProfile(userDetails, OTHER_USER_ID), is(response));
        verify(userLikesRepository).save(eq(userLike(USER_ID, OTHER_USER_ID, null)));
        verify(userMatchesRepository).save(eq(userMatch(USER_ID, OTHER_USER_ID, null)));
    }

    @Test
    void testLikeProfileLikeAlreadyExists() {
        final UserLike savedUserLike = userLike(USER_ID, OTHER_USER_ID, LIKE_ID);

        final CustomUserDetails userDetails = new CustomUserDetails(user(USER_ID, USERNAME, PASSWORD, 1));
        when(userLikesRepository.findByUserIdAndLikedUserId(eq(USER_ID), eq(OTHER_USER_ID)))
                .thenReturn(Optional.of(savedUserLike));

        final LikeAlreadyExistsException exception =
                assertThrows(LikeAlreadyExistsException.class,
                        () -> userService.likeProfile(userDetails, OTHER_USER_ID));
        assertNotNull(exception);
        verify(userLikesRepository, never()).save(any());
        verify(userMatchesRepository, never()).save(any());
    }

    @Test
    void testUnlikeProfile() {
        final CustomUserDetails userDetails = new CustomUserDetails(user(USER_ID, USERNAME, PASSWORD, 1));
        final UserLike userLike = userLike(USER_ID, LIKE_ID);
        when(userLikesRepository.findById(eq(LIKE_ID))).thenReturn(Optional.of(userLike));
        when(userMatchesRepository.findAllByFirstUserIdAndSecondUserId(any(), any())).thenReturn(Optional.empty());
        userService.unlikeProfile(userDetails, LIKE_ID);
        verify(userLikesRepository).delete(eq(userLike));
        verify(userMatchesRepository, never()).delete(any());
    }

    @Test
    void testUnlikeProfileAndRemoveMatch() {
        final UserLike userLike = userLike(USER_ID, OTHER_USER_ID, LIKE_ID);
        final UserMatch userMatch = userMatch(OTHER_USER_ID, USER_ID, MATCH_ID);
        final CustomUserDetails userDetails = new CustomUserDetails(user(USER_ID, USERNAME, PASSWORD, 1));

        when(userLikesRepository.findById(eq(LIKE_ID))).thenReturn(Optional.of(userLike));
        when(userMatchesRepository.findAllByFirstUserIdAndSecondUserId(any(), any()))
                .thenReturn(Optional.of(userMatch));
        userService.unlikeProfile(userDetails, LIKE_ID);
        verify(userLikesRepository).delete(eq(userLike));
        verify(userMatchesRepository).delete(any());
    }

    @Test
    void testUnlikeProfileUserLikeNotFound() {
        final UserLike userLike = userLike(USER_ID, OTHER_USER_ID, LIKE_ID);
        final CustomUserDetails userDetails = new CustomUserDetails(user(USER_ID, USERNAME, PASSWORD, 1));

        when(userLikesRepository.findById(eq(LIKE_ID))).thenReturn(Optional.empty());

        final UserLikeDoesNotExistException exception =
                assertThrows(UserLikeDoesNotExistException.class,
                        () -> userService.unlikeProfile(userDetails, LIKE_ID));
        verify(userLikesRepository, never()).delete(eq(userLike));
        verify(userMatchesRepository, never()).delete(any());
        assertNotNull(exception);
    }
}
