package ru.vsu.cs.oop.pronin_s_v.task1.crypto;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.security.SecureRandom;
import java.util.Base64;

public final class CryptoAESGCM {
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int IV_LEN = 12;      // 96 бит, рекомендация для GCM
    private static final int TAG_LEN = 128;    // бит

    private final SecureRandom rnd = new SecureRandom();

    /** Шифрует строку, возвращает base64(iv) + ":" + base64(ciphertext+tag) */
    public String encrypt(String plain, SecretKey key) {
        try {
            byte[] iv = new byte[IV_LEN];
            rnd.nextBytes(iv);
            Cipher c = Cipher.getInstance(TRANSFORMATION);
            c.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LEN, iv));
            byte[] ct = c.doFinal(plain.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(iv) + ":" +
                   Base64.getEncoder().encodeToString(ct);
        } catch (Exception e) {
            throw new IllegalStateException("Encryption failed", e);
        }
    }

    /** Дешифрует формат base64(iv):base64(ciphertext+tag) */
    public String decrypt(String enc, SecretKey key) {
        try {
            String[] parts = enc.split(":", 2);
            byte[] iv = Base64.getDecoder().decode(parts[0]);
            byte[] ct = Base64.getDecoder().decode(parts[1]);
            Cipher c = Cipher.getInstance(TRANSFORMATION);
            c.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LEN, iv));
            byte[] pt = c.doFinal(ct);
            return new String(pt, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Decryption failed", e);
        }
    }
}