package com.rtmase.board.post.repository;

import com.rtmase.board.post.entity.Post;
import com.rtmase.board.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<Post> findByTitleContainingOrderByCreatedAtDesc(String title, Pageable pageable);

}
