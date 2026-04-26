package com.wordle.common.util;

import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class EncryptUtil {

    public String sha256(String input) {
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
