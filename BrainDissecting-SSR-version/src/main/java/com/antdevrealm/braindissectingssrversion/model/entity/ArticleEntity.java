package com.antdevrealm.braindissectingssrversion.model.entity;

// TEST ARTICLE

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "articles")
public class ArticleEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @OneToMany(targetEntity = CommentEntity.class, mappedBy = "article",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<CommentEntity> comments;

    public ArticleEntity() {
        comments = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public ArticleEntity setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContent() {
        return content;
    }

    public ArticleEntity setContent(String content) {
        this.content = content;
        return this;
    }

    public List<CommentEntity> getComments() {
        return comments;
    }

    public ArticleEntity setComments(List<CommentEntity> comments) {
        this.comments = comments;
        return this;
    }
}
