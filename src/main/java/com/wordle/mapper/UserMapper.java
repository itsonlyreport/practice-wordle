package com.wordle.mapper;

import com.wordle.model.User;

public interface UserMapper {
    User findByUsername(String username);
    User findByGoogleId(String googleId);
    void insertUser(User user);
}