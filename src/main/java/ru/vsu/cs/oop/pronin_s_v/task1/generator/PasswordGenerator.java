package ru.vsu.cs.oop.pronin_s_v.task1.generator;

import java.security.SecureRandom;

public class PasswordGenerator {
    private static final String UPPER   = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER   = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS  = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()-_=+[]{};:,.?/";

    private final SecureRandom rnd = new SecureRandom();

    public String generate(int length) {
        return generate(length, true, true, true, true);
    }

    public String generate(int length, boolean useUpper, boolean useLower,
                           boolean useDigits, boolean useSymbols) {
        if (length < 4) throw new IllegalArgumentException("length must be >= 4");

        StringBuilder alphabet = new StringBuilder();
        if (useUpper) alphabet.append(UPPER);
        if (useLower) alphabet.append(LOWER);
        if (useDigits) alphabet.append(DIGITS);
        if (useSymbols) alphabet.append(SYMBOLS);
        if (alphabet.length() == 0) throw new IllegalArgumentException("empty alphabet");

        String chars = alphabet.toString();
        StringBuilder sb = new StringBuilder(length);

        if (useUpper)  sb.append(UPPER.charAt(rnd.nextInt(UPPER.length())));
        if (useLower)  sb.append(LOWER.charAt(rnd.nextInt(LOWER.length())));
        if (useDigits) sb.append(DIGITS.charAt(rnd.nextInt(DIGITS.length())));
        if (useSymbols)sb.append(SYMBOLS.charAt(rnd.nextInt(SYMBOLS.length())));

        while (sb.length() < length) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }

        char[] a = sb.toString().toCharArray();
        for (int i = a.length - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            char t = a[i]; a[i] = a[j]; a[j] = t;
        }
        return new String(a);
    }
}