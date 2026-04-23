package com.wordle.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long   id;
    private String username;
    private String password;
    private String email;
    private String googleId;
    private String loginType;   // "LOCAL" or "GOOGLE"
}