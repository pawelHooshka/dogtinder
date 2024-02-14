package com.treat.tinderdog.service;

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
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class PetFinderService {

    private static final Object LOCK = new Object();

    private static final String ANIMAL_PROFILES_ENDPOINT = "/animals";

    private final Oauth2PetFinderRestClient petFinderRestTemplate;
    private final UserRepository userRepository;
    private final UserService userService;

    public PetFinderService(
            final Oauth2PetFinderRestClient petFinderRestTemplate,
            final UserRepository userRepository,
            final UserService userService) {
        this.petFinderRestTemplate = petFinderRestTemplate;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public AnimalsList200Response getDogs(final CustomUserDetails userDetails,
                                          final Map<String, Object> queryParameters) {
        synchronized (LOCK) {
            final User user = userRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new UserNotFoundException(userDetails.getUsername()));

            queryParameters.put("page", user.getCurrentProfilesPageNumber());

            final AnimalsList200Response dogs = petFinderRestTemplate.get(
                    ANIMAL_PROFILES_ENDPOINT,
                    queryParameters,
                    AnimalsList200Response.class
            ).getBody();
            removeLoggedInUserIfPresent(user.getId(), dogs);

            user.setCurrentProfilesPageNumber(user.getCurrentProfilesPageNumber() + 1);
            userRepository.save(user);
            return dogs;
        }
    }

    public Animal getMatchedProfile(final CustomUserDetails userDetails, final UUID matchId) {
        final UserMatch userMatch = userService.getUserMatch(userDetails, matchId);
        final long animalId = getMatchedUserId(userDetails.getId(), userMatch);
        final Animal animal = petFinderRestTemplate.get(getAnimalProfileEndpoint(animalId), Animal.class)
                .getBody();

        if (animal != null) {
            return animal;
        } else {
            throw new ProfileNotFoundException(animalId);
        }
    }

    public Animal getLikedProfile(final CustomUserDetails userDetails, final UUID likeId) {
        final UserLike userLike = userService.getUserLike(userDetails, likeId);
        final Animal animal = petFinderRestTemplate
                .get(getAnimalProfileEndpoint(userLike.getLikedUserId()), Animal.class)
                .getBody();

        if (animal != null) {
            return animal;
        } else {
            throw new ProfileNotFoundException(userLike.getLikedUserId());
        }
    }

    private String getAnimalProfileEndpoint(final long profileId) {
        return ANIMAL_PROFILES_ENDPOINT.concat("/" + profileId);
    }

    private long getMatchedUserId(final Long userId, final UserMatch userMatch) {
        return userId.longValue() == userMatch.getFirstUserId()
                ? userMatch.getSecondUserId()
                : userMatch.getFirstUserId();
    }

    private void removeLoggedInUserIfPresent(final Long loggedInUserId,
                                             final AnimalsList200Response suggestions) {
        suggestions.getAnimals().removeIf(dog -> dog.getId() == loggedInUserId.intValue());
    }
}
