package com.antdevrealm.braindissectingssrversion.model.dto.article;

import com.antdevrealm.braindissectingssrversion.model.dto.comment.CommentDTO;

import java.util.ArrayList;
import java.util.List;

public class DisplayArticleDTO {

    private long id;

    private String title;

    private String content;

    private List<CommentDTO> comments;

    public DisplayArticleDTO (String title, String content, long id) {
        this.id= id;
        this.title = title;
        this.content = content;
    }

    public DisplayArticleDTO() {
        comments = new ArrayList<>();
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

    public List<CommentDTO> getComments() {
        return comments;
    }

    public DisplayArticleDTO setComments(List<CommentDTO> comments) {
        this.comments = comments;
        return this;
    }
}
