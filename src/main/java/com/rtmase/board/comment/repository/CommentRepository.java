package com.rtmase.board.comment.repository;

import com.rtmase.board.comment.entity.Comment;
import com.rtmase.board.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByPostOrderByCreatedAt(Post post, Pageable pageable);
}
