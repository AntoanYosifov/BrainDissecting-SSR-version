package com.antdevrealm.braindissectingssrversion.model.dto.article;

public class ArticleDTO {

    private String title;

    private String content;

    public ArticleDTO(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public ArticleDTO() {}

    public String getTitle() {
        return title;
    }

    public ArticleDTO setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContent() {
        return content;
    }

    public ArticleDTO setContent(String content) {
        this.content = content;
        return this;
    }
}
