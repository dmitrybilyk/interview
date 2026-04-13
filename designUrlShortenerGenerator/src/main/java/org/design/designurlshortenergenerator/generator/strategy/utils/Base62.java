package org.design.designurlshortenergenerator.generator.strategy.utils;

public final class Base62 {
    private static final char[] ALPHABET =
      "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final int BASE = ALPHABET.length;

    public static String encode(long value) {
        if (value == 0) return String.valueOf(ALPHABET[0]);
        StringBuilder sb = new StringBuilder();
        while (value > 0) {
            int idx = (int)(value % BASE);
            sb.append(ALPHABET[idx]);
            value /= BASE;
        }
        return sb.reverse().toString();
    }
}
