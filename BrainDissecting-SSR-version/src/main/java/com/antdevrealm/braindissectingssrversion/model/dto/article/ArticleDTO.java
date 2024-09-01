package com.antdevrealm.braindissectingssrversion.model.dto.article;

import com.antdevrealm.braindissectingssrversion.model.dto.comment.CommentDTO;

import java.util.ArrayList;
import java.util.List;

public class ArticleDTO {

    private String title;

    private String content;

    private List<CommentDTO> comments;

    public ArticleDTO(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public ArticleDTO() {
        comments = new ArrayList<>();
    }

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

    public List<CommentDTO> getComments() {
        return comments;
    }

    public ArticleDTO setComments(List<CommentDTO> comments) {
        this.comments = comments;
        return this;
    }
}
