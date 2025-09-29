package ru.vsu.cs.oop.pronin_s_v.task1.crypto;

import org.mindrot.jbcrypt.BCrypt;

public final class Crypto {
    private Crypto() {}

    public static String hashMaster(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    public static boolean verifyMaster(String plainPassword, String storedHash) {
        return BCrypt.checkpw(plainPassword, storedHash);
    }
}