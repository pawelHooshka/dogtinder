package com.treat.tinderdog.data.repository;

import com.treat.tinderdog.data.UserLike;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserLikesPageableRepository extends PagingAndSortingRepository<UserLike, UUID> {

    Optional<List<UserLike>> findAllByUserId(Long userId, Pageable pageable);
}
