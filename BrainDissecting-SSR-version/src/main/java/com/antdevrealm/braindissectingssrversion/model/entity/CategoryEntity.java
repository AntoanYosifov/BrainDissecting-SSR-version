package com.antdevrealm.braindissectingssrversion.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
public class CategoryEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "categories")
    private List<ArticleEntity> articles;

    public CategoryEntity() {
        articles = new ArrayList<>();
    }

    public CategoryEntity(String name) {
       this();
       this.name=name;
    }

    public String getName() {
        return name;
    }

    public CategoryEntity setName(String name) {
        this.name = name;
        return this;
    }

    public List<ArticleEntity> getArticles() {
        return articles;
    }

    public CategoryEntity setArticles(List<ArticleEntity> articles) {
        this.articles = articles;
        return this;
    }
}
