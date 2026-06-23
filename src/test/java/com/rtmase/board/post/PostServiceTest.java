package com.rtmase.board.post;

import com.rtmase.board.post.dto.PostListResponse;
import com.rtmase.board.post.dto.PostResponse;
import com.rtmase.board.post.entity.Post;
import com.rtmase.board.post.repository.PostRepository;
import com.rtmase.board.post.service.PostService;
import com.rtmase.board.user.entity.User;
import com.rtmase.board.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @InjectMocks
    private PostService postService;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("view all posts success")
    void getAllPosts_success() {
        User user = User.builder()
                .email("test@test.com")
                .password("encodedPassword")
                .username("테스터")
                .build();

        Post post = Post.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .user(user)
                .build();

        Page<Post> postPage = new PageImpl<>(List.of(post));
        given(postRepository.findAllByOrderByCreatedAtDesc(any())).willReturn(postPage);

        Page<PostListResponse> result = postService.getAllPages(0, 10);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("테스트 제목");
    }

    @Test
    @DisplayName("single post view success")
    void getPost_success() {
        User user = User.builder()
                .email("test@test.com")
                .password("encodedPassword")
                .username("테스터")
                .build();

        Post post = Post.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .user(user)
                .build();

        given(postRepository.findById(any())).willReturn(Optional.of(post));

        PostResponse result = postService.getPostById(1L);

        assertThat(result.getTitle()).isEqualTo("테스트 제목");
        assertThat(result.getContent()).isEqualTo("테스트 내용");
    }

}
