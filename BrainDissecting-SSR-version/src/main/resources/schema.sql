
CREATE TABLE articles (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          content TEXT NOT NULL,
                          is_favourite BIT,
                          journal_title VARCHAR(255),
                          link VARCHAR(255),
                          article_status ENUM('APPROVED', 'PENDING') NOT NULL,
                          title VARCHAR(255) NOT NULL,
                          PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE categories (
                            id BIGINT NOT NULL AUTO_INCREMENT,
                            name VARCHAR(255) NOT NULL,
                            PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE articles_categories (
                                     article_id BIGINT NOT NULL,
                                     category_id BIGINT NOT NULL
) ENGINE=InnoDB;

CREATE TABLE comments (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          content TEXT NOT NULL,
                          article_id BIGINT NOT NULL,
                          user_id BIGINT NOT NULL,
                          PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE roles (
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       role ENUM('ADMIN', 'MODERATOR', 'USER') NOT NULL,
                       PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE suggested_themes (
                                  id BIGINT NOT NULL AUTO_INCREMENT,
                                  name VARCHAR(255) NOT NULL,
                                  suggested_by_id BIGINT NOT NULL,
                                  PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE user_favourite (
                                user_id BIGINT NOT NULL,
                                favourite_id BIGINT NOT NULL
) ENGINE=InnoDB;

CREATE TABLE users (
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       email VARCHAR(255) NOT NULL,
                       first_name VARCHAR(255),
                       last_name VARCHAR(255),
                       password VARCHAR(255) NOT NULL,
                       user_status ENUM('ACTIVE', 'BANNED') NOT NULL,
                       username VARCHAR(255) NOT NULL,
                       PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE users_roles (
                             user_id BIGINT NOT NULL,
                             role_id BIGINT NOT NULL
) ENGINE=InnoDB;

ALTER TABLE articles ADD CONSTRAINT UK_articles_title UNIQUE (title);
ALTER TABLE categories ADD CONSTRAINT UK_categories_name UNIQUE (name);
ALTER TABLE roles ADD CONSTRAINT UK_roles_role UNIQUE (role);
ALTER TABLE suggested_themes ADD CONSTRAINT UK_suggested_themes_name UNIQUE (name);
ALTER TABLE users ADD CONSTRAINT UK_users_email UNIQUE (email);
ALTER TABLE users ADD CONSTRAINT UK_users_username UNIQUE (username);

ALTER TABLE articles_categories ADD CONSTRAINT FK_articles_categories_article_id FOREIGN KEY (article_id) REFERENCES articles(id);
ALTER TABLE articles_categories ADD CONSTRAINT FK_articles_categories_category_id FOREIGN KEY (category_id) REFERENCES categories(id);

ALTER TABLE comments ADD CONSTRAINT FK_comments_article_id FOREIGN KEY (article_id) REFERENCES articles(id);
ALTER TABLE comments ADD CONSTRAINT FK_comments_user_id FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE suggested_themes ADD CONSTRAINT FK_suggested_themes_suggested_by_id FOREIGN KEY (suggested_by_id) REFERENCES users(id);

ALTER TABLE user_favourite ADD CONSTRAINT FK_user_favourite_user_id FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE user_favourite ADD CONSTRAINT FK_user_favourite_favourite_id FOREIGN KEY (favourite_id) REFERENCES articles(id);

ALTER TABLE users_roles ADD CONSTRAINT FK_users_roles_user_id FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE users_roles ADD CONSTRAINT FK_users_roles_role_id FOREIGN KEY (role_id) REFERENCES roles(id);
