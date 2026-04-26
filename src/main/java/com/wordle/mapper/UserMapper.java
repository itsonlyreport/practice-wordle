package com.wordle.mapper;

import com.wordle.model.User;

import java.util.List;

public interface UserMapper {
    User findByUsername(String username);
    User findByGoogleId(String googleId);
    User findById(Long id);
    List<User> findAll();
    void insertUser(User user);
    void updateUserRole(User user); // 권한 변경
    void updatePassword(User user); // 비밀번호 초기화
    void deleteUser(Long id); // 유저 삭제
}