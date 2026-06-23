package com.rtmase.board.auth;

import com.rtmase.board.auth.dto.LoginRequest;
import com.rtmase.board.auth.dto.SignupRequest;
import com.rtmase.board.auth.jwt.JwtTokenProvider;
import com.rtmase.board.auth.service.AuthService;
import com.rtmase.board.user.entity.User;
import com.rtmase.board.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private RedisTemplate<String, String> redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;

    @Test
    @DisplayName("signup success")
    void signup_success() {
        SignupRequest signupRequest = mock(SignupRequest.class);

        given(signupRequest.getEmail()).willReturn("test3@test.com");
        given(signupRequest.getPassword()).willReturn("asdf1234");
        given(signupRequest.getUsername()).willReturn("테스터");
        given(userRepository.existsByEmail(any())).willReturn(false);
        given(userRepository.existsByUsername(any())).willReturn(false);
        given(passwordEncoder.encode(any())).willReturn("encodedPassword");

        assertThatNoException().isThrownBy(() -> authService.signup(signupRequest));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("signup fail - duplicate email")
    void signup_fail_duplicate_email() {
        SignupRequest request = mock(SignupRequest.class);
        given(request.getEmail()).willReturn("test3@test.com");
        given(userRepository.existsByEmail(any())).willReturn(true);

        assertThatThrownBy(() -> authService.signup(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email already exists");
    }

    @Test
    @DisplayName("login success")
    void login_success() {
        LoginRequest request = mock(LoginRequest.class);
        given(request.getEmail()).willReturn("test3@test.com");
        given(request.getPassword()).willReturn("asdf1234");

        User user = User.builder()
                .email("test3@test.com")
                .password("encodedPassword")
                .username("테스터")
                .build();

        given(userRepository.findByEmail(any())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(any(), any())).willReturn(true);
        given(jwtTokenProvider.createAccessToken(any())).willReturn("accessToken");
        given(jwtTokenProvider.createRefreshToken(any())).willReturn("refreshToken");
        given(redisTemplate.opsForValue()).willReturn(valueOperations);

        assertThatNoException().isThrownBy(() -> authService.login(request));
    }

    @Test
    @DisplayName("login fail - password mismatch")
    void login_fail_wrong_password() {
        LoginRequest request = mock(LoginRequest.class);
        given(request.getEmail()).willReturn("test3@test.com");
        given(request.getPassword()).willReturn("wrongPassword");

        User user = User.builder()
                .email("test3@test.com")
                .password("encodedPassword")
                .username("테스터")
                .build();

        given(userRepository.findByEmail(any())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(any(), any())).willReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Password is incorrect.");
    }

}
