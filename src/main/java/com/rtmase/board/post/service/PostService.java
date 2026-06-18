package com.rtmase.board.post.service;

import com.rtmase.board.post.dto.PostListResponse;
import com.rtmase.board.post.dto.PostRequest;
import com.rtmase.board.post.dto.PostResponse;
import com.rtmase.board.post.entity.Post;
import com.rtmase.board.post.repository.PostRepository;
import com.rtmase.board.user.entity.User;
import com.rtmase.board.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<PostListResponse> getAllPages() {
        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(PostListResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PostResponse getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        return new PostResponse(post);
    }

    @Transactional
    public PostResponse createPost(PostRequest postRequest, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Post post = Post.builder()
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .user(user)
                .build();

        return new PostResponse(postRepository.save(post));
    }

    @Transactional
    public PostResponse updatePost(Long id, PostRequest postRequest, String email) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if(!post.getUser().getEmail().equals(email)) {
            throw new IllegalArgumentException("You do not have permission to edit.");
        }

        post.update(postRequest.getTitle(), postRequest.getContent());
        return new PostResponse(post);
    }

    public void deletePost(Long id, String email) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if(!post.getUser().getEmail().equals(email)) {
            throw new IllegalArgumentException("You do not have permission to delete.");
        }

        postRepository.delete(post);
    }
}
