package com.careerup.pins;

import java.util.*;

/**
 * Given an array of strings words representing an English Dictionary, return
 * the longest word in words that can be built one character at a time by other
 * words in words.
 * 
 * If there is more than one possible answer, return the longest word with the
 * smallest lexicographical order. If there is no answer, return the empty
 * string.
 * 
 * Note that the word should be built from left to right with each additional
 * character being added to the end of a previous word.
 */
public class LongestWordInDict {
    /**
     * Returns the longest word that can be built one character at a time by
     * other words in the given dictionary. When multiple words have the same
     * maximum length, the lexicographically smallest one is returned.
     *
     * Approach:
     * - Sort the array lexicographically so that among equal-length candidates
     *   the lexicographically smallest appears first.
     * - Maintain a set of "buildable" words. Single-character words are
     *   buildable by definition. For longer words, check whether the prefix
     *   (word without the last char) is already in the set.
     * - If a word is buildable and longer than the current result, update it.
     *
     * Complexity: O(n log n) for sorting plus O(total_chars) to scan and check
     * prefixes.
     */
    public String longestWord(String[] words) {
        Arrays.sort(words);
        Set<String> set = new HashSet<>();
        String res = "";
        for (String word : words) {
            if(word.length() == 1 || set.contains(word.substring(0, word.length() - 1))) {
                set.add(word);
                if (word.length() > res.length())
                    res = word;
            }
        }
        return res;
    }

    /**
     * Simple in-file test harness. Runs representative cases and prints PASS/FAIL.
     * Run with:
     *   javac -d out src/com/careerup/pinterest/LongestWordInDict.java
     *   java -cp out com.careerup.pinterest.LongestWordInDict
     */
    public static void main(String[] args) {
        LongestWordInDict solver = new LongestWordInDict();
        Object[][] tests = new Object[][]{
            { new String[]{"w","wo","wor","worl","world"}, "world" },
            { new String[]{"a","banana","app","appl","ap","apply","apple"}, "apple" },
            { new String[]{"ab","bc","abc"}, "" }, // no chain from single-char
            { new String[]{"a","b","ab","abc"}, "abc" },
            { new String[]{}, "" }
        };

        int failures = 0;
        for (Object[] t : tests) {
            String[] words = (String[]) t[0];
            String expected = (String) t[1];
            String got = solver.longestWord(words);
            if ((expected == null && got == null) || (expected != null && expected.equals(got))) {
                System.out.printf("PASS: %s -> %s%n", Arrays.toString(words), got);
            } else {
                System.out.printf("FAIL: %s expected=%s got=%s%n", Arrays.toString(words), expected, got);
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
