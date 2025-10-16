package ru.vsu.cs.oop.pronin_s_v.task1.crypto;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

public final class KeyFile {
    private final Path file;

    public KeyFile(Path file) {
        this.file = Objects.requireNonNull(file, "file");
    }

    /** Вернёт ключ из файла, либо сгенерирует новый и сохранит. */
    public SecretKey getOrCreate() {
        try {
            if (Files.exists(file)) {
                String b64 = Files.readString(file, StandardCharsets.UTF_8).trim();
                byte[] raw = Base64.getDecoder().decode(b64);
                return new SecretKeySpec(raw, "AES");
            } else {
                Path parent = file.toAbsolutePath().getParent();
                if (parent != null && !Files.exists(parent)) Files.createDirectories(parent);
                byte[] raw = new byte[32]; // 256 бит
                new SecureRandom().nextBytes(raw);
                String b64 = Base64.getEncoder().encodeToString(raw);
                Files.writeString(file, b64, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
                return new SecretKeySpec(raw, "AES");
            }
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось получить/создать ключ: " + file, e);
        }
    }
}