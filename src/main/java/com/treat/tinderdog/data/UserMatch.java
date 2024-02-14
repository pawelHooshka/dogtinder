package com.treat.tinderdog.data;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(
        name = "user_matches",
        indexes = {
                @Index(name = "first_user_id_index", columnList = "firstUserId"),
                @Index(name = "second_user_id_index", columnList = "secondUserId")
        }
)
@EntityListeners(AuditingEntityListener.class)
@Data
public class UserMatch {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(nullable = false)
    private Long firstUserId;
    @Column(nullable = false)
    private Long secondUserId;
    @Column(nullable = false)
    @CreatedDate
    private Timestamp createdAt;
}
