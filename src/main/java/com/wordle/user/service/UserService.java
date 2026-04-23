package com.wordle.user.service;

import com.wordle.mapper.UserMapper;
import com.wordle.model.User;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class UserService {
    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    // 일반 로그인
    public User login(String username, String password) {
        var user = userMapper.findByUsername(username);
        if (user == null) return null;
        if (!user.getPassword().equals(sha256(password))) return null;
        return user;
    }

    // 일반 회원가입
    public boolean register(String username, String password, String email) {
        // 중복 체크
        if (userMapper.findByUsername(username) != null) return false;

        var user = User.builder()
                .username(username)
                .password(sha256(password))
                .email(email)
                .loginType("LOCAL")
                .build();

        userMapper.insertUser(user);
        return true;
    }

    // 구글 로그인 (없으면 자동 가입)
    public User loginWithGoogle(String googleId, String email) {
        var user = userMapper.findByGoogleId(googleId);

        if (user == null) {
            user = User.builder()
                    .googleId(googleId)
                    .email(email)
                    .loginType("GOOGLE")
                    .build();
            userMapper.insertUser(user);
            user = userMapper.findByGoogleId(googleId);
        }

        return user;
    }

    // SHA-256 해시
    public static String sha256(String input) {
        try {
            var digest = MessageDigest.getInstance("SHA-256");
            var bytes  = digest.digest(input.getBytes());
            var sb     = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 오류", e);
        }
    }
}
