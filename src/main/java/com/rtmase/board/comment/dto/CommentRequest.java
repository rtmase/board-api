package com.rtmase.board.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CommentRequest {

    @NotBlank(message = "Please enter your comment.")
    private String content;
}
