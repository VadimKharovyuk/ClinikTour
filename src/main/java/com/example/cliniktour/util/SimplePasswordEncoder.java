package com.example.cliniktour.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Простой класс для шифрования паролей с использованием SHA-256
 */
public class SimplePasswordEncoder {

    /**
     * Шифрует пароль с использованием SHA-256
     * @param rawPassword сырой пароль
     * @return зашифрованный пароль
     */
    public String encode(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Ошибка шифрования пароля", e);
        }
    }

    /**
     * Проверяет, совпадает ли сырой пароль с зашифрованным
     * @param rawPassword сырой пароль
     * @param encodedPassword зашифрованный пароль
     * @return true, если пароли совпадают
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return encode(rawPassword).equals(encodedPassword);
    }
}