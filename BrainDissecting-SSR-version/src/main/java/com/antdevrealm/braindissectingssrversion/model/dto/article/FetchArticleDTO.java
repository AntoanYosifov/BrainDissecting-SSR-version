package com.antdevrealm.braindissectingssrversion.model.dto.article;

import com.antdevrealm.braindissectingssrversion.model.dto.comment.DisplayCommentDTO;

import java.util.ArrayList;
import java.util.List;

public class FetchArticleDTO {

    private String title;

    private String content;

    private List<DisplayCommentDTO> comments;

    public FetchArticleDTO(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public FetchArticleDTO() {
        comments = new ArrayList<>();
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

    public List<DisplayCommentDTO> getComments() {
        return comments;
    }

    public FetchArticleDTO setComments(List<DisplayCommentDTO> comments) {
        this.comments = comments;
        return this;
    }
}
