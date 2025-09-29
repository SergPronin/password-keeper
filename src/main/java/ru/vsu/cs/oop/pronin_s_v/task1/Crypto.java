package ru.vsu.cs.oop.pronin_s_v.task1;

import org.mindrot.jbcrypt.BCrypt;


public class Crypto {

    String hashPassword(String plainPassword){
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }


}
