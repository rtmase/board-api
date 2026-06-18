package com.rtmase.board.auth.service;

import com.rtmase.board.auth.dto.LoginRequest;
import com.rtmase.board.auth.dto.SignupRequest;
import com.rtmase.board.auth.dto.TokenResponse;
import com.rtmase.board.auth.jwt.JwtTokenProvider;
import com.rtmase.board.user.entity.User;
import com.rtmase.board.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public void signup(SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = User.builder()
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .username(signupRequest.getUsername())
                .build();

        userRepository.save(user);
    }

    @Transactional
    public TokenResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Your email is incorrect."));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw  new IllegalArgumentException("Password is incorrect.");
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        redisTemplate.opsForValue()
                .set("RT:"+user.getEmail(),refreshToken,7, TimeUnit.DAYS);

        return new TokenResponse(accessToken, refreshToken);
    }

    @Transactional
    public void logout(String email) {
        redisTemplate.delete("RT:"+email);
    }

    @Transactional
    public TokenResponse refreshToken(String refreshToken) {
        if(!jwtTokenProvider.validationToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token.");
        }

        String email = jwtTokenProvider.getEmail(refreshToken);
        String savedToken = redisTemplate.opsForValue().get("RT:" + email);

        if (savedToken == null || !savedToken.equals(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token.");
        }

        String newAccessToken = jwtTokenProvider.createAccessToken(email);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(email);

        redisTemplate.opsForValue()
                .set("RT:"+email,newRefreshToken,7, TimeUnit.DAYS);

        return new TokenResponse(newAccessToken, newRefreshToken);
    }
}
