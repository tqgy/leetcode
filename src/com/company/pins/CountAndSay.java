package com.careerup.pins;

import java.util.ArrayList;
import java.util.List;

/**
 * Count-and-say sequence utilities.
 *
 * The count-and-say sequence is defined as follows:
 * <ul>
 * <li>countAndSay(1) = "1"</li>
 * <li>For n > 1, countAndSay(n) is the run-length encoding (RLE) of
 * countAndSay(n-1)</li>
 * </ul>
 *
 * Run-length encoding replaces consecutive runs of the same digit with
 * "{count}{digit}". Example: "3322251" -> "23321511" ("33" -> "23", "222" ->
 * "32", "5" -> "15", "1" -> "11").
 *
 * Example:
 * 
 * <pre>
 * Input: n = 4
 * Output: "1211"
 * Explanation:
 *   countAndSay(1) = "1"
 *   countAndSay(2) = "11"
 *   countAndSay(3) = "21"
 *   countAndSay(4) = "1211"
 * </pre>
 *
 * Constraints: 1 <= n <= 30
 *
 * This class provides an iterative implementation which constructs each term
 * from the previous one using simple run-length encoding.
 */
public class CountAndSay {

    /**
     * Return the nth element of the count-and-say sequence using an iterative
     * approach.
     *
     * Time complexity: O(n * m) where m is the average length of intermediate
     * strings (for the given constraints this is efficient). Space complexity: O(m)
     * to build each term.
     *
     * @param n 1-based index in the sequence
     * @return the nth count-and-say string
     */
    public static String countAndSay(int n) {
        String cur = "1";

        // Iterate to generate the nth sequence term
        for (int i = 1; i < n; i++) {
            StringBuilder next = new StringBuilder();
            int count = 1;

            // Describe the current sequence
            for (int j = 0; j < cur.length(); j++) {
                // Count consecutive digits
                if (j < cur.length() - 1 && cur.charAt(j) == cur.charAt(j + 1)) {
                    count++;
                } else {
                    next.append(count);
                    next.append(cur.charAt(j));

                    // Reset count for the next digit
                    count = 1;
                }
            }
            cur = next.toString();
        }

        return cur;
    }

    /**
     * Recover all original digit strings that could produce the given
     * count-and-say encoding after exactly one encoding pass.
     *
     * Input format (encoded): a concatenation of groups in the form
     *   [count][digit]
     * where:
     * - count is an integer in the range 1..99 (1 or 2 digits, no leading
     *   zeros), and
     * - digit is a single character '0'..'9' to be repeated.
     *
     * Because counts may be one or two digits, the encoded string can be
     * ambiguous; this method returns all original strings consistent with the
     * encoding by performing a DFS that tries count widths of 1 or 2 digits,
     * validates counts (no leading '0', count >= 1), and expands each group
     * into repeated digits.
     *
     * Example: "23321511" -> ["3322251"]
     *
     * @param encoded a count-and-say encoded string composed of [count][digit]
     * @return list of all possible original digit strings (order is unspecified)
     */
    public List<String> reverseCountAndSay(String encoded) {
        List<String> res = new ArrayList<>();
        dfs(encoded, 0, "", res);
        return res;
    }

    /**
     * DFS helper parsing {@code encoded} from {@code pos} and building a
     * candidate original string {@code s}. When {@code pos} reaches the end of
     * {@code encoded}, {@code s} is a complete candidate and is added to {@code res}.
     *
     * At each step the method tries count widths of 1 and 2 digits (if enough
     * characters remain). For each width it:
     * - parses the count substring (must not start with '0' and must be >= 1),
     * - reads the following digit char, and
     * - appends that digit count times to the candidate and recurses.
     *
     * This carefully explores all valid decodings while pruning invalid counts.
     */
    public void dfs(String encoded, int pos, String s, List<String> res) {
        if (pos == encoded.length()) {
            res.add(s);
            return;
        }
        // try counts of width 1 or 2 (if available)
        for (int i = 1; i <= 2; i++) {
            if (pos + i >= encoded.length())
                break;
            // extract count substring and parse it
            int count = Integer.parseInt(encoded.substring(pos, pos + i));
            // counts must not start with '0' and must be positive
            if (encoded.charAt(pos) == '0' || count < 1)
                continue;
            String part = "";
            char digit = encoded.charAt(pos + i);
            // repeat the digit 'count' times
            for (int j = 0; j < count; j++) {
                part += digit;
            }
            // recurse after consuming count digits + the digit char
            dfs(encoded, pos + i + 1, s + part, res);
        }
    }

    /**
     * Simple in-file tests for both the forward `countAndSay` (static)
     * and the reverse `reverseCountAndSay` methods. Prints PASS/FAIL per case.
     *
     * Run with:
     *   javac -d out src/com/careerup/pinterest/CountAndSay.java
     *   java -cp out com.careerup.pinterest.CountAndSay
     */
    public static void main(String[] args) {
        int failures = 0;

        // Tests for countAndSay (static method)
        Object[][] forward = new Object[][]{
            {1, "1"},
            {2, "11"},
            {3, "21"},
            {4, "1211"},
            {5, "111221"}
        };
        for (Object[] t : forward) {
            int n = (int) t[0];
            String expected = (String) t[1];
            String got = countAndSay(n);
            if (!expected.equals(got)) {
                System.out.printf("FAIL countAndSay(%d): expected=%s got=%s%n", n, expected, got);
                failures++;
            } else {
                System.out.printf("PASS countAndSay(%d) -> %s%n", n, got);
            }
        }

        // Tests for reverseCountAndSay (instance method)
        CountAndSay inst = new CountAndSay();
        Object[][] reverse = new Object[][]{
            {"23321511", "3322251"}, // the example in the class javadoc
            {"11", "1"},             // "1" -> "11"
            {"1211", "21"}          // "21" -> "1211"
        };
        for (Object[] t : reverse) {
            String enc = (String) t[0];
            String expected = (String) t[1];
            List<String> got = inst.reverseCountAndSay(enc);
            if (got.contains(expected)) {
                System.out.printf("PASS reverse(%s) contains %s%n", enc, expected);
            } else {
                System.out.printf("FAIL reverse(%s): expected to contain %s but got=%s%n", enc, expected, got);
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
