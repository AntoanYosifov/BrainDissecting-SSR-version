package com.antdevrealm.braindissectingssrversion.model.dto.article;

import com.antdevrealm.braindissectingssrversion.model.dto.comment.DisplayCommentDTO;

import java.util.ArrayList;
import java.util.List;

public class FetchArticleDTO {

    private String title;

    private String content;

    private String link;

    public FetchArticleDTO() {}

    public FetchArticleDTO(String title, String content) {
        this.title = title;
        this.content = content;
    }


    public String getTitle() {
        return title;
    }

    public FetchArticleDTO setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContent() {
        return content;
    }

    public FetchArticleDTO setContent(String content) {
        this.content = content;
        return this;
    }

    public String getLink() {
        return link;
    }

    public FetchArticleDTO setLink(String link) {
        this.link = link;
        return this;
    }
}
