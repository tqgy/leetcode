package com.careerup.pinterest;

import java.util.*;
import java.util.stream.Collectors;

/*
 * NestedSetEquality
 *
 * Compare two nested sets represented as lists. Elements are either strings
 * (atomic) or nested lists (which themselves follow the same rules).
 *
 * Rules:
 * - Treat lists as unordered sets at every level (order doesn't matter).
 * - Duplicates are ignored (set semantics).
 * - Equality is recursive: strings must match exactly; nested lists compare as sets.
 */
public class NestedSetEquality {

    public static boolean areEqual(Object a, Object b) {
        return normalize(a).equals(normalize(b));
    }

    // Normalize an object into a comparable form:
    // - Strings are returned unchanged (atomic)
    // - Lists are normalized recursively, duplicates removed, and sorted by
    // string representation so order doesn't matter
    private static Object normalize(Object obj) {
        if (obj instanceof String) {
            return obj; // atomic
        }

        // Treat object as a list and normalize recursively
        List<?> list = (List<?>) obj;
        List<Object> normalized = new ArrayList<>();

        for (Object element : list) {
            normalized.add(normalize(element));
        }

        // Remove duplicates (set semantics) then sort by string representation
        List<Object> uniqList = normalized.stream()
            .distinct()
            .sorted(Comparator.comparing(Object::toString))
            .collect(Collectors.toList());

        return uniqList;
    }

    public static void main(String[] args) {
        int failures = 0;

        Object[][] tests = new Object[][] {
                // simple reordering
                { List.of("a", List.of("b", "c")), List.of(List.of("c", "b"), "a"), true },

                // duplicates ignored
                { List.of("a", "a", "b"), List.of("b", "a"), true },

                // nested duplicates ignored
                { List.of("x", List.of("a", "b", "a")), List.of(List.of("b", "a"), "x"), true },

                // deeper nesting
                { List.of(List.of("a", "b"), List.of("c")), List.of(List.of("b", "a"), List.of("c")), true },

                // not equal
                { List.of("a", "b"), List.of("a", "c"), false },

                // empty lists
                { List.of(), List.of(), true },

                // string vs string
                { "hello", "hello", true }, { "hello", "world", false } };

        int i = 0;
        for (Object[] t : tests) {
            i++;
            Object left = t[0];
            Object right = t[1];
            boolean expected = (boolean) t[2];

            boolean got = areEqual(left, right);
            if (got == expected) {
                System.out.printf("PASS %d -> %b%n", i, got);
            } else {
                System.out.printf("FAIL %d -> got=%b expected=%b (left=%s right=%s)%n", i, got, expected, left, right);
                failures++;
            }
        }

        if (failures > 0) {
            System.out.printf("%d test(s) failed.%n", failures);
            System.exit(1);
        } else {
            System.out.println("All tests passed.");
        }
    }
}
