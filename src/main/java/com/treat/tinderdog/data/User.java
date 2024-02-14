package com.treat.tinderdog.data;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(
        name = "users",
        indexes = @Index(name = "user_username_index", columnList = "username")
)
@Data
public class User {

    @Id
    private Long id;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(columnDefinition = "INTEGER NOT NULL DEFAULT 1")
    private Integer currentProfilesPageNumber;
}
