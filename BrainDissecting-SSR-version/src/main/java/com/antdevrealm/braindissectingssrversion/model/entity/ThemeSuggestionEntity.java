package com.antdevrealm.braindissectingssrversion.model.entity;

import com.antdevrealm.braindissectingssrversion.model.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "suggested_themes")
public class ThemeSuggestionEntity extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne(optional = false)
    private UserEntity suggestedBy;

    public ThemeSuggestionEntity() {}

    public String getName() {
        return name;
    }

    public ThemeSuggestionEntity setName(String name) {
        this.name = name;
        return this;
    }


    public UserEntity getSuggestedBy() {
        return suggestedBy;
    }

    public ThemeSuggestionEntity setSuggestedBy(UserEntity suggestedBy) {
        this.suggestedBy = suggestedBy;
        return this;
    }
}
