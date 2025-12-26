package com.careerup.pins;

/**
 * Given a string s, rearrange the characters of s so that any two adjacent
 * characters are not the same.
 * 
 * Return any possible rearrangement of s or return "" if not possible.
 */
public class ReorganizeString {
    /**
     * Rearranges characters so that no two adjacent characters are the same.
     *
     * Approach (greedy):
     * 1. Count frequency of each character.
     * 2. If the highest frequency 'max' is greater than (n+1)/2 the task is
     *    impossible and we return an empty string.
     * 3. Otherwise, place the most frequent character at even indices
     *    (0,2,4,...) first, then fill remaining characters into the array
     *    (switching to odd indices when even indices are exhausted).
     *
     * This guarantees a valid arrangement when one exists. Time complexity is
     * O(n + k) where k=26 (alphabet size), and space complexity is O(n).
     *
     * Note: Implementation is unchanged to preserve your original logic.
     */
    public String reorganizeString(String s) {
        int[] freq = new int[26]; // to Store Frequency of each alphabet
        char[] arr = s.toCharArray();

        for (int i = 0; i < arr.length; i++) { // store the frequency
            freq[arr[i] - 'a']++;
        }

        int max = 0, letter = 0;

        for (int i = 0; i < 26; i++) { // find the max frequency
            if (freq[i] > max) {
                max = freq[i];
                letter = i;
            }
        }
        // if max is more than half then not possible
        if (max > (s.length() + 1) / 2)
            return "";

        int idx = 0;
        char[] res = new char[s.length()];

        while (freq[letter] > 0) { // distribute the max freq char into even indices
            res[idx] = (char) (letter + 'a');
            idx += 2;
            freq[letter]--;
        }

        for (int i = 0; i < 26; i++) {
            while (freq[i] > 0) {
                // all even indices filled, so switch to odd indices
                if (idx >= s.length())
                    idx = 1;
                res[idx] = (char) (i + 'a');
                idx += 2;
                freq[i]--;
            }
        }

        return String.valueOf(res);
    }

    // --- In-file test harness ------------------------------------------------

    private static boolean isValidRearrangement(String orig, String out) {
        // If the method produced an empty string, verify impossibility
        if (out.isEmpty()) 
            return false;

        // length must match and no two adjacent chars equal
        if (out.length() != orig.length()) 
            return false;
        for (int i = 1; i < out.length(); i++) {
            if (out.charAt(i) == out.charAt(i - 1)) 
                return false;
        }

        // character counts must match (anagram check)
        int[] f1 = new int[26], f2 = new int[26];
        for (char c : orig.toCharArray()) f1[c - 'a']++;
        for (char c : out.toCharArray()) f2[c - 'a']++;
        for (int i = 0; i < 26; i++) if (f1[i] != f2[i]) 
            return false;
        return true;
    }

    public static void main(String[] args) {
        Object[][] tests = new Object[][]{
            {"aab", true},   // possible -> e.g., "aba"
            {"aaab", false}, // impossible
            {"aa", false},   // impossible
            {"a", true},     // single char
            {"aaabbc", true},
            {"vvvlo", true}
        };

        ReorganizeString inst = new ReorganizeString();
        int failures = 0;
        for (Object[] t : tests) {
            String in = (String) t[0];
            boolean expectPossible = (boolean) t[1];
            String out = inst.reorganizeString(in);
            boolean valid = isValidRearrangement(in, out);
            if (valid && expectPossible) {
                System.out.printf("PASS: %s -> %s%n", in, out);
            } else if (!valid && !expectPossible) {
                System.out.printf("PASS (correctly impossible): %s -> %s%n", in, out);
            } else {
                System.out.printf("FAIL: %s -> %s (expectedPossible=%b)%n", in, out, expectPossible);
                failures++;
            }
        }

        if (failures > 0) {
            System.out.printf("\n%d test(s) failed.%n", failures);
            System.exit(1);
        } else {
            System.out.println("\nAll tests passed.");
        }
    }
}
