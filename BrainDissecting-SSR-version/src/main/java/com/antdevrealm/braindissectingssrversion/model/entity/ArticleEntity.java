package com.antdevrealm.braindissectingssrversion.model.entity;

// TEST ARTICLE

import com.antdevrealm.braindissectingssrversion.model.enums.Status;
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

    @Column(name = "journal_title")
    private String journalTitle;

    private String link;

    @Column(name = "article_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    // Change fetch type to lazy after adding functionality for seeing the comments onClick!
    @OneToMany(targetEntity = CommentEntity.class, mappedBy = "article",
            orphanRemoval = true)
    private List<CommentEntity> comments;

    @ManyToMany
    @JoinTable(name = "articles_categories",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private List<CategoryEntity> categories;

    public ArticleEntity() {
        comments = new ArrayList<>();
        categories = new ArrayList<>();
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

    public String getJournalTitle() {
        return journalTitle;
    }

    public ArticleEntity setJournalTitle(String journalTitle) {
        this.journalTitle = journalTitle;
        return this;
    }

    public String getLink() {
        return link;
    }

    public ArticleEntity setLink(String link) {
        this.link = link;
        return this;
    }

    public List<CommentEntity> getComments() {
        return comments;
    }

    public ArticleEntity setComments(List<CommentEntity> comments) {
        this.comments = comments;
        return this;
    }

    public List<CategoryEntity> getCategories() {
        return categories;
    }

    public ArticleEntity setCategories(List<CategoryEntity> categories) {
        this.categories = categories;
        return this;
    }

    public Status getStatus() {
        return status;
    }

    public ArticleEntity setStatus(Status articleStatus) {
        this.status = articleStatus;
        return this;
    }
}
