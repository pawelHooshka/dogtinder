package com.treat.tinderdog.model;

import java.sql.Timestamp;
import java.util.UUID;

public record UserLikeWithPossibleMatchResponse(
        Long userId,
        Long likedUserId,
        UUID likeId,
        UUID matchId,
        Timestamp likeCreatedAt,
        Timestamp matchCreatedAt) {

}
