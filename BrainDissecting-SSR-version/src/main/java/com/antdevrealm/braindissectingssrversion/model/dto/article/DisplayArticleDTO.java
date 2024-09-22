package com.antdevrealm.braindissectingssrversion.model.dto.article;

import com.antdevrealm.braindissectingssrversion.model.dto.comment.DisplayCommentDTO;

import java.util.ArrayList;
import java.util.List;

public class DisplayArticleDTO {

    private long id;

    private String title;

    private String content;

    private String journalTitle;

    private String link;

    private List<String> categories;

    private List<DisplayCommentDTO> comments;

    public DisplayArticleDTO() {
        categories = new ArrayList<>();
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

    public String getJournalTitle() {
        return journalTitle;
    }

    public DisplayArticleDTO setJournalTitle(String journalTitle) {
        this.journalTitle = journalTitle;
        return this;
    }

    public String getLink() {
        return link;
    }

    public DisplayArticleDTO setLink(String link) {
        this.link = link;
        return this;
    }

    public List<String> getCategories() {
        return categories;
    }

    public DisplayArticleDTO setCategories(List<String> categories) {
        this.categories = categories;
        return this;
    }

    public List<DisplayCommentDTO> getComments() {
        return comments;
    }

    public DisplayArticleDTO setComments(List<DisplayCommentDTO> comments) {
        this.comments = comments;
        return this;
    }

    @Override
    public String toString() {
        return "DisplayArticleDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", comments=" + comments +
                '}';
    }
}
