package com.rtmase.board.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PostRequest {

    @NotBlank(message = "Please enter title.")
    private String title;

    @NotBlank(message = "Please enter content.")
    private String content;
}
