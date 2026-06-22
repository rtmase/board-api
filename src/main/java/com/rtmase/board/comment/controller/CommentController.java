package com.rtmase.board.comment.controller;

import com.rtmase.board.comment.dto.CommentRequest;
import com.rtmase.board.comment.dto.CommentResponse;
import com.rtmase.board.comment.entity.Comment;
import com.rtmase.board.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
@Tag(name = "Comment", description = "댓글 관련 API")
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    @Operation(summary = "Retrieve comment")
    public ResponseEntity<Page<CommentResponse>> getComments(@PathVariable("postId") Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(commentService.getComments(postId, page, size));
    }

    @PostMapping
    @Operation(summary = "Create comment")
    public ResponseEntity<CommentResponse> createComment(@PathVariable("postId") Long postId,
            @Valid @RequestBody CommentRequest commentRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(commentService.createComment(postId, commentRequest, userDetails.getUsername()));
    }

    @DeleteMapping
    @Operation(summary = "Delete comment")
    public ResponseEntity<String> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long CommentId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        commentService.deleteComment(postId, userDetails.getUsername());
        return ResponseEntity.ok("comment has been deleted.");
    }
}
