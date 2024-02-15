package com.treat.tinderdog;

import com.treat.tinderdog.data.User;
import com.treat.tinderdog.data.UserLike;
import com.treat.tinderdog.data.UserMatch;
import com.treat.tinderdog.model.Animal;
import com.treat.tinderdog.model.AnimalsList200Response;
import org.assertj.core.util.Lists;

import java.util.UUID;

public class TestUtils {

    public static UserLike userLike(final long userId, final UUID likeId) {
        final UserLike userLike = new UserLike();
        userLike.setId(likeId);
        userLike.setUserId(userId);
        userLike.setLikedUserId(1L);
        return userLike;
    }

    public static UserLike userLike(final long userId, final long otherUserId, final UUID likeId) {
        final UserLike userLike = new UserLike();
        userLike.setId(likeId);
        userLike.setUserId(userId);
        userLike.setLikedUserId(otherUserId);
        return userLike;
    }

    public static UserMatch userMatch(final long userId, final UUID matchId) {
        final UserMatch userMatch = new UserMatch();
        userMatch.setId(matchId);
        userMatch.setFirstUserId(userId);
        userMatch.setSecondUserId(1L);
        return userMatch;
    }

    public static UserMatch userMatch(final long userId, final long secondUserId, final UUID matchId) {
        final UserMatch userMatch = new UserMatch();
        userMatch.setId(matchId);
        userMatch.setFirstUserId(userId);
        userMatch.setSecondUserId(secondUserId);
        return userMatch;
    }

    public static AnimalsList200Response animals() {
        final AnimalsList200Response dogs = new AnimalsList200Response();
        dogs.setAnimals(Lists.newArrayList());
        return dogs;
    }

    public static Animal animal(final int userId, final String age) {
        final Animal animal = new Animal();
        animal.setId(userId);
        animal.setAge(age);
        return animal;
    }

    public static AnimalsList200Response animals(final int userId, final String age) {
        final AnimalsList200Response dogs = new AnimalsList200Response();
        dogs.setAnimals(Lists.newArrayList(animal(userId, age)));
        return dogs;
    }

    public static User user(final long userId, final String username, final String password, final int page) {
        final User user = new User();
        user.setId(userId);
        user.setUsername(username);
        user.setPassword(password);
        user.setCurrentProfilesPageNumber(page);
        return user;
    }
}
