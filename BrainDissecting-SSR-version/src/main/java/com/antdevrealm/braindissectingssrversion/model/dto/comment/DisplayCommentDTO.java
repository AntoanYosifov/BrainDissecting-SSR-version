package com.antdevrealm.braindissectingssrversion.model.dto.comment;

public class DisplayCommentDTO {

    private long id;

    private String content;

    private String author;

    private long authorId;

    public DisplayCommentDTO() {}

    public long getId() {
        return id;
    }

    public DisplayCommentDTO setId(long id) {
        this.id = id;
        return this;
    }

    public String getContent() {
        return content;
    }

    public DisplayCommentDTO setContent(String content) {
        this.content = content;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public DisplayCommentDTO setAuthor(String author) {
        this.author = author;
        return this;
    }

    public long getAuthorId() {
        return authorId;
    }

    public DisplayCommentDTO setAuthorId(long authorId) {
        this.authorId = authorId;
        return this;
    }
}
