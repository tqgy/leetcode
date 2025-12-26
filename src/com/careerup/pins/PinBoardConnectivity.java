package com.careerup.pins;

import java.util.*;

/**
 * You are managing a collection of boards, where each board is represented as a
 * list of distinct positive integers called pins. Two pins are considered
 * related if you can move from one pin to another by traversing through shared
 * boards, either directly or through a series of hops across pins and boards. A
 * pin may appear on multiple boards.
 * 
 * Given boards, a list of boards, and two pins pin1 and pin2, implement a
 * function to determine whether the two pins are related. Return true if they
 * are related, and false otherwise.
 */
public class PinBoardConnectivity {
    /**
     * Simple Union-Find (disjoint set) structure for connecting pins.
     *
     * parent maps a pin -> its parent/root representative. The structure
     * supports "find" with path compression and a straightforward "union"
     * operation (no union-by-rank is used here to keep the implementation
     * minimal and clear).
     */
    static class UnionFind {
        Map<Integer, Integer> parent = new HashMap<>();
        
        /**
         * Find the root representative of x. If x does not exist in the parent
         * map yet it is initialized to be its own parent (singleton set).
         * Path compression flattens the tree to make subsequent lookups faster.
         */
        public int find(int x) {
            parent.putIfAbsent(x, x);
            if (x != parent.get(x)){
                // recursively find and compress path
                parent.put(x, find(parent.get(x)));
            }
            return parent.get(x);
        }

        /**
         * Union the sets that contain x and y by attaching root(x) to root(y).
         * This merges the two components into one. Note: no rank/size heuristic
         * is used — the root of x is simply pointed to the root of y.
         */
        public void union(int x, int y) {
            if(find(x) != find(y)) {
                parent.put(find(x), find(y));
            }
        }
    }

    /**
     * Determine whether two pins are related via shared boards.
     *
     * Approach:
     * - For each board, we union every pin on that board with the board's
     *   first pin. This ensures all pins appearing on the same board become
     *   part of the same connected component.
     * - After processing all boards, two pins are related iff their
     *   Union-Find representatives (roots) are equal.
     *
     * Note: the method intentionally preserves the original behavior and does
     * not perform defensive null checks on the input arguments.
     */
    public static boolean arePinsRelated(List<List<Integer>> boards, int pin1, int pin2) {
        UnionFind uf = new UnionFind();
        for(List<Integer> board : boards) {
            // connect each pin on this board to the board's first pin; this
            // merges all pins on the board into a single connected component
            for(int i = 1; i < board.size(); i++) {
                uf.union(board.get(0), board.get(i));
            }
        }
        // two pins are related if they have the same representative root
        return uf.find(pin1) == uf.find(pin2);
    }

    /**
     * Simple in-file test harness. Runs a variety of small scenarios and prints
     * PASS/FAIL for each case. The tests reflect the current implementation's
     * behavior (for example, the method treats the same pin number as "related"
     * even if it doesn't appear on any board).
     *
     * Run with:
     *   javac -d out src/com/careerup/pinterest/PinBoardConnectivity.java
     *   java -cp out com.careerup.pinterest.PinBoardConnectivity
     */
    public static void main(String[] args) {
        Object[][] cases = new Object[][]{
            // boards, pin1, pin2, expectedResult (Boolean or String "throws")
            { Arrays.asList(Arrays.asList(1,2,3), Arrays.asList(4,5,2)), 1, 5, true }, // connected via 2
            { Arrays.asList(Arrays.asList(1), Arrays.asList(2,3)), 1, 3, false }, // disconnected
            { Arrays.asList(), 1, 1, true }, // same pin, not present on any board -> treated as related
            { Arrays.asList(Arrays.asList(1,2), Arrays.asList(2,3), Arrays.asList(4,5)), 1, 3, true }, // multi-hop
            { Arrays.asList(Arrays.asList(1), Arrays.asList(1,2)), 1, 2, true }, // single-element board + shared pin
            { Arrays.asList(Arrays.asList(10,20), Arrays.asList(30,40)), 20, 30, false }, // disjoint groups
            { null, 1, 2, "throws" } // null boards -> expecting NullPointerException from current impl
        };

        int failures = 0;
        for (Object[] c : cases) {
            @SuppressWarnings("unchecked")
            List<List<Integer>> boards = (List<List<Integer>>) c[0];
            int p1 = (int) c[1];
            int p2 = (int) c[2];
            Object expected = c[3];

            try {
                boolean got = arePinsRelated(boards, p1, p2);
                if (expected instanceof Boolean) {
                    boolean exp = (Boolean) expected;
                    if (got == exp) {
                        System.out.printf("PASS: pins %d,%d with boards=%s -> %b%n", p1, p2, boards, got);
                    } else {
                        System.out.printf("FAIL: pins %d,%d with boards=%s expected=%b got=%b%n", p1, p2, boards, exp, got);
                        failures++;
                    }
                } else {
                    System.out.printf("FAIL (expected exception but got result): pins %d,%d with boards=%s -> %b%n", p1, p2, boards, got);
                    failures++;
                }
            } catch (Exception e) {
                if ("throws".equals(expected)) {
                    System.out.printf("PASS (threw %s): pins %d,%d with boards=%s%n", e.getClass().getSimpleName(), p1, p2, boards);
                } else {
                    System.out.printf("FAIL (threw %s): pins %d,%d with boards=%s%n", e.getClass().getSimpleName(), p1, p2, boards);
                    failures++;
                }
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
