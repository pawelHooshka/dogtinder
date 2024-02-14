CREATE TABLE user_likes
(
    id            UUID      NOT NULL,
    user_id       BIGINT    NOT NULL,
    liked_user_id BIGINT    NOT NULL,
    created_at    TIMESTAMP NOT NULL,
    CONSTRAINT pk_user_likes PRIMARY KEY (id)
);

CREATE TABLE user_matches
(
    id             UUID      NOT NULL,
    first_user_id  BIGINT    NOT NULL,
    second_user_id BIGINT    NOT NULL,
    created_at     TIMESTAMP NOT NULL,
    CONSTRAINT pk_user_matches PRIMARY KEY (id)
);

CREATE TABLE users
(
    id                           BIGINT        NOT NULL,
    username                     VARCHAR(255)  NOT NULL,
    password                     VARCHAR(255)  NOT NULL,
    current_profiles_page_number INT DEFAULT 1 NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_username UNIQUE (username);

CREATE INDEX first_user_id_index ON user_matches (first_user_id);

CREATE INDEX second_user_id_index ON user_matches (second_user_id);

CREATE UNIQUE INDEX user_id_index ON user_likes (user_id, liked_user_id);

CREATE INDEX user_username_index ON users (username);