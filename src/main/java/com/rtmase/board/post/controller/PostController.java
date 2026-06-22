package com.rtmase.board.post.controller;

import com.rtmase.board.post.dto.PostListResponse;
import com.rtmase.board.post.dto.PostRequest;
import com.rtmase.board.post.dto.PostResponse;
import com.rtmase.board.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "Post", description = "게시글 관련 API")
public class PostController {

    private final PostService postService;

    @GetMapping
    @Operation(summary = "Retrieve all posts")
    public ResponseEntity<Page<PostListResponse>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(postService.getAllPages(page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve post")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @PostMapping
    @Operation(summary = "Create post")
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody PostRequest postRequest
            , @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postService.createPost(postRequest,userDetails.getUsername()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modify post")
    public ResponseEntity<PostResponse> updatePost(@Valid @RequestBody PostRequest postRequest
            , @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postService.updatePost(id, postRequest,userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "delete post")
    public ResponseEntity<String> deletePost(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        postService.deletePost(id, userDetails.getUsername());
        return ResponseEntity.ok("Post has been deleted");
    }
}
