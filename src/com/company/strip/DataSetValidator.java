package com.careerup.strip;

/**
 * -----------------------------------------------------------------------------
 * Dataset Validation Problem
 * -----------------------------------------------------------------------------
 *
 * You are given a CSV dataset as a multi-line string. Each row contains:
 *     col1, col2, col3, col4, col5, col6
 *
 * For each row (excluding the header), determine whether it is:
 *
 *     VERIFIED: <col2_value>
 *     NOT VERIFIED: <col2_value>
 *
 * A row is VERIFIED only if it satisfies ALL rules:
 *
 * 1. No Empty Fields:
 *      - All 6 columns must be present and non-empty after trimming.
 *
 * 2. Column 5 Length Constraint:
 *      - col5 length must be between 5 and 31 characters (inclusive).
 *
 * 3. Forbidden Words in col2:
 *      - col2 must NOT contain (case-insensitive):
 *          ["company", "firm", "co.", "corporation", "group"]
 *
 * 4. Word Match Rule:
 *      - Split col2, col4, col5 into lowercase words by whitespace.
 *      - Remove "llc" and "inc".
 *      - At least 50% of col2's words must appear in col4 OR col5.
 *
 * Output must preserve row order.
 * -----------------------------------------------------------------------------
 */

import java.util.*;

public class DataSetValidator {

    private static final Set<String> FORBIDDEN = Set.of(
            "company", "firm", "co.", "corporation", "group"
    );

    private static final Set<String> IGNORE = Set.of("llc", "inc");

    /** Main validation entry */
    public static List<String> validate(String csv) {
        List<String> results = new ArrayList<>();
        List<String> lines = csv.lines().toList();

        // Skip header (line 0)
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue; // skip blank lines

            String[] cols = line.split(","); // keep empty fields
            String col2 = cols.length > 1 ? cols[1].trim() : "";

            boolean ok = isVerified(cols);
            results.add((ok ? "VERIFIED: " : "NOT VERIFIED: ") + col2);
        }
        return results;
    }

    /** Apply all validation rules */
    private static boolean isVerified(String[] cols) {
        // Rule 1: Must have 6 non-empty fields
        if (cols.length != 6) return false;
        for (String c : cols) {
            if (c.trim().isEmpty()) return false;
        }

        String col2 = cols[1].trim();
        String col4 = cols[3].trim();
        String col5 = cols[4].trim();

        // Rule 2: col5 length constraint
        if (col5.length() < 5 || col5.length() > 31) return false;

        // Rule 3: forbidden words in col2
        String col2Lower = col2.toLowerCase();
        for (String bad : FORBIDDEN) {
            if (col2Lower.contains(bad)) return false;
        }

        // Rule 4: 50% word match with col4 or col5
        Set<String> w2 = cleanWords(col2);
        Set<String> w4 = cleanWords(col4);
        Set<String> w5 = cleanWords(col5);

        if (w2.isEmpty()) return false;

        int matches4 = countMatches(w2, w4);
        int matches5 = countMatches(w2, w5);

        double required = Math.ceil(w2.size() * 0.5);

        return matches4 >= required || matches5 >= required;
    }

    /** Split into lowercase words, remove ignored words */
    private static Set<String> cleanWords(String s) {
        Set<String> out = new HashSet<>();
        for (String w : s.toLowerCase().split("\\s+")) {
            if (!w.isEmpty() && !IGNORE.contains(w)) {
                out.add(w);
            }
        }
        return out;
    }

    /** Count how many words from a appear in b */
    private static int countMatches(Set<String> a, Set<String> b) {
        int count = 0;
        for (String w : a) {
            if (b.contains(w)) count++;
        }
        return count;
    }

    // -------------------------------------------------------------------------
    // Test Cases
    // -------------------------------------------------------------------------
    public static void main(String[] args) {
        String data = """
                col1,col2,col3,col4,col5,col6
                a,land water,c,d,land water LLC,f
                a,Good Company,c,d,land water,f
                a,b,c,d,e,f
                1,2,3,,5,6
                """;

        List<String> results = validate(data);
        results.forEach(System.out::println);

        // Expected:
        // VERIFIED: land water
        // NOT VERIFIED: Good Company
        // NOT VERIFIED: b
        // NOT VERIFIED: 2
    }
}

