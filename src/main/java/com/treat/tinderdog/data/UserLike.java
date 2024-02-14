package com.treat.tinderdog.data;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(
        name = "user_likes",
        indexes = @Index(name = "user_id_index", columnList = "user_id, liked_user_id", unique = true)
)
@EntityListeners(AuditingEntityListener.class)
@Data
public class UserLike {

    @Id
    @GeneratedValue
    private UUID id;
    @Column(nullable = false)
    private Long userId;
    //user's id rather than User object - to avoid reading usernames and passwords of other users.
    //User object contains also username and password.
    @Column(nullable = false)
    private Long likedUserId;
    @Column(nullable = false)
    @CreatedDate
    private Timestamp createdAt;
}
