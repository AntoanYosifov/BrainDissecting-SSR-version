package com.antdevrealm.braindissectingssrversion.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "comments")
public class CommentEntity extends BaseEntity {

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(targetEntity = UserEntity.class, optional = false)
    private UserEntity user;

    @ManyToOne(targetEntity = ArticleEntity.class, optional = false)
    private ArticleEntity article;

    public CommentEntity() {}

    public String getContent() {
        return content;
    }

    public CommentEntity setContent(String content) {
        this.content = content;
        return this;
    }

    public UserEntity getUser() {
        return user;
    }

    public CommentEntity setUser(UserEntity user) {
        this.user = user;
        return this;
    }

    public ArticleEntity getArticle() {
        return article;
    }

    public CommentEntity setArticle(ArticleEntity article) {
        this.article = article;
        return this;
    }
}
