package com.treat.tinderdog.data.repository;

import com.treat.tinderdog.data.UserLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserLikesRepository extends JpaRepository<UserLike, UUID> {

    Optional<List<UserLike>> findAllByUserId(Long userId);
    Optional<UserLike> findByUserIdAndLikedUserId(Long userId, Long likedUserId);
}
