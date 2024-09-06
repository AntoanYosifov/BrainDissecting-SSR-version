package com.antdevrealm.braindissectingssrversion.model.dto.comment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AddCommentDTO {

    @NotNull
    @Size(min = 1)
    private String content;

    public AddCommentDTO() {}

    public String getContent() {
        return content;
    }

    public AddCommentDTO setContent(String content) {
        this.content = content;
        return this;
    }
}
