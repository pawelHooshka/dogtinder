package com.treat.tinderdog.data.repository;

import com.treat.tinderdog.data.UserMatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserMatchesRepository extends JpaRepository<UserMatch, UUID> {

    Optional<UserMatch> findAllByFirstUserIdAndSecondUserId(Long firstUserId, Long secondUserId);
    Optional<List<UserMatch>> findAllByFirstUserIdOrSecondUserId(Long firstUserId, Long secondUserId);
}
