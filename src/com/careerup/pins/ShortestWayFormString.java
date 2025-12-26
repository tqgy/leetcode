package com.careerup.pins;

/**
 * Computes the minimum number of subsequences from a source string required
 * to form a target string by concatenating subsequences taken in-order.
 *
 * Note: The method implementation below is left unchanged per request; the
 * tests in the harness exercise the current behavior (including surprising
 * cases like an empty target or empty source).
 */
public class ShortestWayFormString {

    /**
     * Returns the minimum number of subsequences from {@code sourceString}
     * needed to form {@code targetString}, or -1 if it's impossible.
     *
     * Important: This comment does not change the implementation below — the
     * method body is intentionally left exactly as it was.
     *
     * @param sourceString source string used to create subsequences
     * @param targetString target string to form
     * @return minimum number of subsequences or -1 if impossible
     */
    public int ShortestWay(String sourceString, String targetString) {
        int cnt = 1, sidx = 0, tidx = 0;
        while(tidx < targetString.length()) {
            int currentTidx = tidx;
            if(sidx == sourceString.length()) {
                cnt++;
                sidx = 0;
            }
            for(int i = 0; i < sourceString.length() & tidx < targetString.length(); i++){
                if(sourceString.charAt(sidx) == targetString.charAt(tidx)) {
                    tidx++;
                }
                sidx++;
            }
            
            if(tidx == currentTidx) return -1;
        }
        return cnt;
    }

    /**
     * In-file test harness. Runs a set of test cases exercising the current
     * implementation and prints PASS/FAIL for each case.
     *
     * Run with:
     *   javac -d out src/com/careerup/pinterest/ShortestWayFormString.java
     *   java -cp out com.careerup.pinterest.ShortestWayFormString
     */
    public static void main(String[] args) {
        ShortestWayFormString solver = new ShortestWayFormString();

        // Test cases: {source, target, expectedResult (Integer|null), expectException}
        Object[][] cases = new Object[][]{
                {"abc", "abcbc", 2, false},
                {"abc", "d", -1, false},
                {"abc", "", 1, false},   // current impl returns 1 for empty target
                {"", "", 1, false},      // current impl returns 1 when both empty
                {"", "a", -1, false},   // calling with empty source+non-empty target throws
                {"aaa", "aaaaaa", 2, false},
                {"xyz", "xxyyzzx", 5, false}
        };

        int failures = 0;
        for (Object[] c : cases) {
            String s = (String) c[0];
            String t = (String) c[1];
            Integer expected = (Integer) c[2];
            boolean expectException = (boolean) c[3];

            try {
                int got = solver.ShortestWay(s, t);
                if (expectException) {
                    System.out.printf("FAIL (expected exception): source=%s target=%s got=%d%n", s, t, got);
                    failures++;
                } else if (expected != null && got == expected) {
                    System.out.printf("PASS: source=%s target=%s -> %d%n", s, t, got);
                } else if (expected == null) {
                    System.out.printf("INFO: source=%s target=%s -> %d (no expected value)%n", s, t, got);
                } else {
                    System.out.printf("FAIL: source=%s target=%s expected=%d got=%d%n", s, t, expected, got);
                    failures++;
                }
            } catch (Exception e) {
                if (expectException) {
                    System.out.printf("PASS (threw): source=%s target=%s -> %s%n", s, t, e.getClass().getSimpleName());
                } else {
                    System.out.printf("FAIL (threw): source=%s target=%s -> %s%n", s, t, e.getClass().getSimpleName());
                    failures++;
                }
            }
        }

        if (failures > 0) {
            System.out.printf("%n%d test(s) failed.%n", failures);
            System.exit(1);
        } else {
            System.out.println("\nAll tests passed (according to current expectations).");
        }
    }
}
