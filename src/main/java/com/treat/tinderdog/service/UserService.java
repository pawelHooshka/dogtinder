package com.treat.tinderdog.service;

import com.treat.tinderdog.data.CustomUserDetails;
import com.treat.tinderdog.data.UserLike;
import com.treat.tinderdog.data.UserMatch;
import com.treat.tinderdog.data.repository.UserLikesPageableRepository;
import com.treat.tinderdog.data.repository.UserLikesRepository;
import com.treat.tinderdog.data.repository.UserMatchesRepository;
import com.treat.tinderdog.data.repository.UserRepository;
import com.treat.tinderdog.model.UserLikeWithPossibleMatchResponse;
import com.treat.tinderdog.model.UserLikes;
import com.treat.tinderdog.model.UserMatches;
import com.treat.tinderdog.service.exception.*;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    private static final String SORT_DIRECTION_DESC = "DESC";

    private final UserRepository userRepository;
    private final UserLikesRepository userLikesRepository;
    private final UserLikesPageableRepository userLikesPageableRepository;
    private final UserMatchesRepository userMatchesRepository;

    public UserService(final UserRepository userRepository,
                       final UserLikesRepository userLikesRepository,
                       final UserLikesPageableRepository userLikesPageableRepository,
                       final UserMatchesRepository userMatchesRepository) {
        this.userRepository = userRepository;
        this.userLikesRepository = userLikesRepository;
        this.userLikesPageableRepository = userLikesPageableRepository;
        this.userMatchesRepository = userMatchesRepository;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return userRepository.findUserByUsername(username)
                .map(user -> new CustomUserDetails(user))
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("User does not exist user=%s", username)
                ));
    }

    public UserLikes getUserLikes(final CustomUserDetails userDetails) {
        return userLikesRepository.findAllByUserId(userDetails.getId())
                .map(UserLikes::new)
                .orElse(new UserLikes(new ArrayList<>()));
    }

    public UserLikes getUserLikes(final CustomUserDetails userDetails,
                                  final Integer page,
                                  final Integer limit,
                                  final String sortProperty,
                                  final String sortDirection) {
        if (sortProperty == null) {
            return userLikesPageableRepository
                    .findAllByUserId(userDetails.getId(), PageRequest.of(page, limit))
                    .map(UserLikes::new)
                    .orElse(new UserLikes(new ArrayList<>()));
        } else if (SORT_DIRECTION_DESC.equalsIgnoreCase(sortDirection)) {
            return userLikesPageableRepository
                    .findAllByUserId(
                            userDetails.getId(), PageRequest.of(page, limit, Sort.by(sortProperty).descending()))
                    .map(UserLikes::new)
                    .orElse(new UserLikes(new ArrayList<>()));
        } else {
            return userLikesPageableRepository
                    .findAllByUserId(
                            userDetails.getId(), PageRequest.of(page, limit, Sort.by(sortProperty).ascending()))
                    .map(UserLikes::new)
                    .orElse(new UserLikes(new ArrayList<>()));
        }
    }

    public UserMatches getUserMatches(final CustomUserDetails userDetails) {
        return userMatchesRepository.findAllByFirstUserIdOrSecondUserId(
                userDetails.getId(), userDetails.getId())
                .map(UserMatches::new)
                .orElse(new UserMatches(new ArrayList<>()));
    }

    public UserMatch getUserMatch(final CustomUserDetails userDetails,
                                  final UUID matchId) {
        return userMatchesRepository.findById(matchId)
                .filter(match -> validateUserMatch(match, userDetails.getId()))
                .orElseThrow(() ->
                        new NotAuthorisedToAccessResourceException(userDetails, "USER_MATCH", matchId));
    }

    public UserLike getUserLike(final CustomUserDetails userDetails,
                                final UUID likeId) {
        return userLikesRepository.findById(likeId)
                .filter(userLike -> validateUserLike(userLike, userDetails.getId()))
                .orElseThrow(() ->
                        new NotAuthorisedToAccessResourceException(userDetails, "USER_LIKE", likeId));
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public UserLikeWithPossibleMatchResponse likeProfile(final CustomUserDetails userDetails, final long profileId) {
        if (userDetails.getId() == profileId) {
            throw new UserIdAndLikedProfileIdViolationException(
                    userDetails.getUsername(),
                    userDetails.getId(),
                    profileId);
        }
        if (!doesLikeExist(userDetails, profileId)) {
            return createLike(userDetails, profileId);
        } else {
            throw new LikeAlreadyExistsException(userDetails.getUsername(), profileId);
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public UserLike unlikeProfile(final CustomUserDetails userDetails, final UUID likeId) {
        final UserLike userLike = userLikesRepository
                .findById(likeId)
                .orElseThrow(() -> new UserLikeDoesNotExistException(userDetails.getUsername(), likeId));
        userLikesRepository.delete(userLike);
        removeMatchIfPresent(userLike);

        return userLike;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void unlikeAll(final CustomUserDetails userDetails) {
        final List<UserLike> userLikes = userLikesRepository
                .findAllByUserId(userDetails.getId())
                .orElse(new ArrayList<>());
        userLikesRepository.deleteAll(userLikes);
        removeAllMatches(userDetails.getId());
    }

    private void removeMatchIfPresent(final UserLike removedUserLike) {
        userMatchesRepository.findAllByFirstUserIdAndSecondUserId(
                removedUserLike.getUserId(),
                removedUserLike.getLikedUserId())
                .or(() -> userMatchesRepository.findAllByFirstUserIdAndSecondUserId(
                        removedUserLike.getLikedUserId(),
                        removedUserLike.getUserId()))
                .ifPresent(userMatch -> userMatchesRepository.delete(userMatch));
    }

    private void removeAllMatches(final Long userId) {
        userMatchesRepository.findAllByFirstUserIdOrSecondUserId(userId, userId)
                .ifPresent(userMatches -> userMatchesRepository.deleteAll(userMatches));
    }

    private UserLikeWithPossibleMatchResponse createLike(final CustomUserDetails userDetails, final Long profileId) {
        final UserLike userLike = new UserLike();
        userLike.setUserId(userDetails.getId());
        userLike.setLikedUserId(profileId);
        final UserLike savedUserLike = userLikesRepository.save(userLike);

        if (savedUserLike != null) {
            return createMatchOnMutualLikeIfNotPresent(userDetails.getUsername(), savedUserLike);
        } else {
            throw new FailedToSaveUsersLikeException(userDetails.getUsername());
        }
    }

    private UserLikeWithPossibleMatchResponse createMatchOnMutualLikeIfNotPresent(
            final String username,
            final UserLike savedUserLike) {
        if (doesMatchAlreadyExistForUserLike(savedUserLike)) {
            throw new MatchAlreadyExistsException(username, savedUserLike.getLikedUserId());
        } else if (doesOppositeLikeExist(savedUserLike)) {
            return createMutualMatch(username, savedUserLike);
        } else {
            return new UserLikeWithPossibleMatchResponse(
                    savedUserLike.getUserId(),
                    savedUserLike.getLikedUserId(),
                    savedUserLike.getId(),
                    null,
                    savedUserLike.getCreatedAt(),
                    null);
        }
    }

    private boolean doesLikeExist(final CustomUserDetails userDetails, final Long profileId) {
        return userLikesRepository
                .findByUserIdAndLikedUserId(userDetails.getId(), profileId)
                .isPresent();
    }

    private boolean doesOppositeLikeExist(final UserLike savedUserLike) {
        return userLikesRepository.findByUserIdAndLikedUserId(
                        savedUserLike.getLikedUserId(),
                        savedUserLike.getUserId())
                .isPresent();
    }

    private boolean doesMatchAlreadyExistForUserLike(final UserLike savedUserLike) {
        return userMatchesRepository.findAllByFirstUserIdAndSecondUserId(
                        savedUserLike.getUserId(),
                        savedUserLike.getLikedUserId())
                .isPresent() || userMatchesRepository.findAllByFirstUserIdAndSecondUserId(
                        savedUserLike.getLikedUserId(),
                        savedUserLike.getUserId())
                .isPresent();
    }

    private UserLikeWithPossibleMatchResponse createMutualMatch(final String username,
                                                                final UserLike savedUserLike) {
        final UserMatch savedUserMatch = createUserMatchFromUserLike(savedUserLike);
        if (savedUserMatch != null) {
            return new UserLikeWithPossibleMatchResponse(
                    savedUserLike.getUserId(),
                    savedUserLike.getLikedUserId(),
                    savedUserLike.getId(),
                    savedUserMatch.getId(),
                    savedUserLike.getCreatedAt(),
                    savedUserMatch.getCreatedAt());
        } else {
            throw new FailedToSaveUsersMatchException(username, savedUserLike.getLikedUserId());
        }
    }

    private UserMatch createUserMatchFromUserLike(final UserLike savedUserLike) {
        final UserMatch userMatch = new UserMatch();
        userMatch.setFirstUserId(savedUserLike.getUserId());
        userMatch.setSecondUserId(savedUserLike.getLikedUserId());
        return userMatchesRepository.save(userMatch);
    }

    private boolean validateUserMatch(final UserMatch userMatch, final long userId) {
        return userId == userMatch.getFirstUserId() || userId == userMatch.getSecondUserId();
    }

    private boolean validateUserLike(final UserLike userLike, final long userId) {
        return userId == userLike.getUserId();
    }
}
