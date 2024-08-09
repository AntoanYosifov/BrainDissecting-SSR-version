package com.antdevrealm.braindissectingssrversion.model.dto.article;

public class DisplayArticleDTO {

    private String title;

    private String content;

    public DisplayArticleDTO() {}

    public String getTitle() {
        return title;
    }

    public DisplayArticleDTO setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContent() {
        return content;
    }

    public DisplayArticleDTO setContent(String content) {
        this.content = content;
        return this;
    }
}
