package com.antdevrealm.braindissectingssrversion.model.dto.comment;

public class CommentDTO {

    private String content;

    private String author;

    public CommentDTO() {}

    public String getContent() {
        return content;
    }

    public CommentDTO setContent(String content) {
        this.content = content;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public CommentDTO setAuthor(String author) {
        this.author = author;
        return this;
    }
}
