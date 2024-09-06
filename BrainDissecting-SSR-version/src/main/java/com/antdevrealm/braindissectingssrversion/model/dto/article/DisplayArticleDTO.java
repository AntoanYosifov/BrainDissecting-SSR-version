package com.antdevrealm.braindissectingssrversion.model.dto.article;

import com.antdevrealm.braindissectingssrversion.model.dto.comment.DisplayCommentDTO;

import java.util.ArrayList;
import java.util.List;

public class DisplayArticleDTO {

    private long id;

    private String title;

    private String content;

    private List<DisplayCommentDTO> comments;

    public DisplayArticleDTO() {
        comments = new ArrayList<>();
    }

    public DisplayArticleDTO (String title, String content, long id) {
        this();
        this.id= id;
        this.title = title;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public DisplayArticleDTO setId(long id) {
        this.id = id;
        return this;
    }

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

    public List<DisplayCommentDTO> getComments() {
        return comments;
    }

    public DisplayArticleDTO setComments(List<DisplayCommentDTO> comments) {
        this.comments = comments;
        return this;
    }
}
