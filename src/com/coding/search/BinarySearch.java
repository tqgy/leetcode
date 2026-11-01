package com.carrerup.search;

import java.util.function.Predicate;

public class BinarySearch {
    /*
     * ✔ Works with half‑open intervals [left, right) 
     * ✔ Handles all monotonic conditions 
     * ✔ Same structure for arrays, time, capacity, etc.
     * 
     * If search for lower bound, then the condition should be mid >= target
     * If search for upper bound, then the condition should be mid > target
     * [1,2,3,3,3,3,4,5]
     * lower bound of 3 is 2
     * upper bound of 3 is 6
     */
    int binarySearchFirstTrue(int n, Predicate<Integer> condition) {
        int left = 0, right = n; // search space: [left, right)
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (condition.test(mid)) {
                right = mid; // mid might be the first true
            } else {
                left = mid + 1; // mid is false → go right
            }
        }
        return left; // first true index (or n if none)
    }

    // test in main for the target value and lower bound and upper bound
    public static void main(String[] args) {
        BinarySearch bs = new BinarySearch();
        System.out.println(bs.binarySearchFirstTrue(10, x -> x * x >= 10));
    }

}
