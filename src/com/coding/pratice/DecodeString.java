package com.carrerup.pratice;

import java.util.*;

public class DecodeString {

    public static String decodeString(String encoded) {
        StringBuilder decoded = new StringBuilder();
        int i = 0;

        while (i < encoded.length()) {
            char c = encoded.charAt(i);

            // Expect a letter
            if (!Character.isLetter(c)) {
                throw new IllegalArgumentException("Invalid format: expected a letter at position " + i);
            }
            i++;

            // Collect digits after the letter
            StringBuilder numBuilder = new StringBuilder();
            while (i < encoded.length() && Character.isDigit(encoded.charAt(i))) {
                numBuilder.append(encoded.charAt(i));
                i++;
            }

            if (numBuilder.length() == 0) {
                throw new IllegalArgumentException("Invalid format: missing number after letter " + c);
            }

            int count = Integer.parseInt(numBuilder.toString());

            // Append repeated characters
            for (int j = 0; j < count; j++) {
                decoded.append(c);
            }
        }

        return decoded.toString();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String encodedInput = scanner.nextLine().trim();
        System.out.println(decodeString(encodedInput));
        scanner.close();
    }
}

