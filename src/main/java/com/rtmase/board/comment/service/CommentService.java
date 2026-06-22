package com.rtmase.board.comment.service;

import com.rtmase.board.comment.dto.CommentRequest;
import com.rtmase.board.comment.dto.CommentResponse;
import com.rtmase.board.comment.entity.Comment;
import com.rtmase.board.comment.repository.CommentRepository;
import com.rtmase.board.post.entity.Post;
import com.rtmase.board.post.repository.PostRepository;
import com.rtmase.board.user.entity.User;
import com.rtmase.board.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<CommentResponse> getComments(Long postId, int page, int size) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found."));

        Pageable pageable = PageRequest.of(page, size);

        return commentRepository.findByPostOrderByCreatedAt(post,pageable)
                .map(CommentResponse::new);
    }

    @Transactional
    public CommentResponse createComment(Long postId, CommentRequest commentRequest, String email) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found."));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        Comment comment = Comment.builder()
                .content(commentRequest.getContent())
                .post(post)
                .user(user)
                .build();

        return new CommentResponse(commentRepository.save(comment));
    }

    @Transactional
    public void deleteComment(Long commentId,String email) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found."));

        if (!comment.getUser().getEmail().equals(email)) {
            throw  new IllegalArgumentException("You do not have permission to delete comment.");
        }

        commentRepository.delete(comment);
    }
}
