package com.wordle.user.service;

import com.wordle.common.util.EncryptUtil;
import com.wordle.mapper.UserMapper;
import com.wordle.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper  userMapper;

    @Autowired
    private EncryptUtil encryptUtil;

    // 일반 로그인
    public User login(String username, String password) {
        var user = userMapper.findByUsername(username);
        if (user == null) return null;
        if (!user.getPassword().equals(encryptUtil.sha256(password))) return null;
        return user;
    }

    // 일반 회원가입
    public boolean register(String username, String password, String email) {
        // 중복 체크
        if (userMapper.findByUsername(username) != null) return false;

        var user = User.builder()
                .username(username)
                .password(encryptUtil.sha256(password))
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

    // 비밀번호 초기화 (관리자용)
    public String resetPassword(Long requesterId, Long targetId) {

        // 1. 요청자가 관리자인지 체크
        var requester = userMapper.findById(requesterId);
        if (requester == null || !requester.isAdmin()) {
            throw new IllegalStateException("관리자만 비밀번호를 초기화할 수 있습니다.");
        }

        // 2. 자기 자신 초기화 방지
        if (requesterId.equals(targetId)) {
            throw new IllegalArgumentException("자신의 비밀번호는 초기화할 수 없습니다.");
        }

        // 3. 대상 유저 존재 여부 체크
        var target = userMapper.findById(targetId);
        if (target == null) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }

        // 4. LOCAL 로그인 유저만 초기화 가능
        if (!"LOCAL".equals(target.getLoginType())) {
            throw new IllegalArgumentException("구글 로그인 유저는 비밀번호를 초기화할 수 없습니다.");
        }

        // 5. 임시 비밀번호 생성 및 저장
        var tempPassword = generateTempPassword();
        var user = User.builder()
                .id(targetId)
                .password(encryptUtil.sha256(tempPassword))
                .build();
        userMapper.updatePassword(user);

        return tempPassword;
    }

    // 임시 비밀번호 생성
    private String generateTempPassword() {
        var chars  = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        var random = new java.util.Random();
        var sb     = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
