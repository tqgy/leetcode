package com.careerup.pins;

import java.util.*;

/**
 * A sparse matrix is a two-dimensional array in which most elements are zero.
 * Storing such matrices in their original form can waste a significant amount
 * of space. To address this, design a class that efficiently stores sparse
 * matrices while supporting key operations such as addition and multiplication
 * using minimum space.
 */
public class SparseMatrix {

    private final int rows, cols;
    // Use a nested map: row -> (col -> value). This avoids packing indices into a
    // single long key and makes code clearer and less error-prone.
    private final Map<Integer, Map<Integer, Integer>> data;

    public SparseMatrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.data = new HashMap<>();
    }

    public void set(int r, int c, int val) {
        if (val == 0) {
            Map<Integer, Integer> row = data.get(r);
            if (row != null) {
                row.remove(c);
                if (row.isEmpty())
                    data.remove(r);
            }
        } else {
            data.computeIfAbsent(r, k -> new HashMap<>()).put(c, val);
        }
    }

    public int get(int r, int c) {
        Map<Integer, Integer> row = data.get(r);
        return row == null ? 0 : row.getOrDefault(c, 0);
    }

    public SparseMatrix add(SparseMatrix other) {
        SparseMatrix result = new SparseMatrix(rows, cols);

        // Add all entries from this matrix
        this.data.forEach((r, row) -> row.forEach((c, val) -> result.set(r, c, val)));

        // Add all entries from the other matrix (accumulate into result)
        other.data.forEach((r, row) -> row.forEach((c, val) -> result.set(r, c, result.get(r, c) + val)));

        return result;
    }

    public SparseMatrix multiply(SparseMatrix other) {
        SparseMatrix result = new SparseMatrix(rows, other.cols);

        // For each non-zero A(i,k), iterate row k of B and accumulate A(i,k)*B(k,j)
        this.data.forEach((i, rowA) -> rowA.forEach((k, valA) -> {
            Map<Integer, Integer> rowB = other.data.get(k);
            if (rowB != null) {
                rowB.forEach((j, valB) -> result.set(i, j, result.get(i, j) + valA * valB));
            }
        }));

        return result;
    }

    // --- Test helpers & in-file tests --------------------------------------

    /**
     * Returns an unmodifiable view of a row's contents (col -> value). Useful for
     * testing and debugging.
     */
    public Map<Integer, Integer> getRow(int r) {
        return data.getOrDefault(r, Collections.emptyMap());
    }

    public static void main(String[] args) {
        int failures = 0;

        // Test addition
        SparseMatrix A = new SparseMatrix(2, 2);
        A.set(0, 0, 1);
        A.set(0, 1, 2);
        A.set(1, 0, 3);

        SparseMatrix B = new SparseMatrix(2, 2);
        B.set(0, 1, 4);
        B.set(1, 1, 5);

        SparseMatrix C = A.add(B);
        boolean ok = C.get(0, 0) == 1 && C.get(0, 1) == 6 && C.get(1, 0) == 3 && C.get(1, 1) == 5;
        if (ok)
            System.out.println("PASS add");
        else {
            System.out.println("FAIL add");
            failures++;
        }

        // Test multiplication
        // A = [1 2; 3 0], B = [0 4; 0 5]
        SparseMatrix D = A.multiply(B);
        boolean ok2 = D.get(0, 0) == 0 && D.get(0, 1) == 14 && D.get(1, 0) == 0 && D.get(1, 1) == 12;
        if (ok2)
            System.out.println("PASS multiply");
        else {
            System.out.println("FAIL multiply");
            failures++;
        }

        // Test zero-removal behavior
        C.set(0, 1, 0);
        if (C.get(0, 1) == 0)
            System.out.println("PASS zero removal");
        else {
            System.out.println("FAIL zero removal");
            failures++;
        }

        if (failures > 0) {
            System.out.printf("%n%d test(s) failed.%n", failures);
            System.exit(1);
        } else {
            System.out.println("\nAll tests passed.");
        }
    }
}
