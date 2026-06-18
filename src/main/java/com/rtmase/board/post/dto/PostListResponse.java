package com.rtmase.board.post.dto;

import com.rtmase.board.post.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostListResponse {

    private Long id;
    private String title;
    private String username;
    private LocalDateTime createdAt;

    public  PostListResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.username = post.getUser().getUsername();
        this.createdAt = post.getCreatedAt();
    }

}
