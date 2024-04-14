package com.github.dsx137.jable.random;

import java.util.Locale;
import java.util.Random;

public class RandomSecret extends RandomGenerator<String> {
    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase(Locale.getDefault());
    private static final String NUMBER = "0123456789012345678901234567890123456789";

    @Override
    protected String generate(byte[] seed, int length, Random random) {
        StringBuilder secret = new StringBuilder(length);
        String allChars = CHAR_LOWER + CHAR_UPPER + NUMBER;

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(allChars.length());
            secret.append(allChars.charAt(index));
        }

        return secret.toString();
    }
}
