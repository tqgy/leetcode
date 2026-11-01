package com.careerup.tools;

import java.util.Arrays;

/**
 * Utility class for Binary Search operations.
 */
public class BinarySearch {

    /**
     * Standard Binary Search.
     * Finds the index of the target value in a sorted array.
     *
     * @param nums   The sorted input array.
     * @param target The value to search for.
     * @return The index of the target if found, otherwise -1.
     */
    public int search(int[] nums, int target) {
        int left = 0;
        int right = nums.length - 1; // Search space: [left, right]

        while (left <= right) {
            int mid = left + (right - left) / 2; // Prevent overflow
            if (nums[mid] == target) {
                return mid; // Target found
            } else if (nums[mid] < target) {
                left = mid + 1; // Target is in the right half
            } else {
                right = mid - 1; // Target is in the left half
            }
        }
        return -1; // Target not found
    }

    /**
     * Lower Bound.
     * Finds the first index `i` such that nums[i] >= target.
     *
     * @param nums   The sorted input array.
     * @param target The value to search for.
     * @return The first index where nums[index] >= target. Returns nums.length if all elements are smaller than target.
     */
    public int lowerBound(int[] nums, int target) {
        int left = 0;
        int right = nums.length; // Search space: [left, right)

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] < target) {
                // mid is clearly smaller than target, so the answer must be to the right
                left = mid + 1;
            } else {
                // nums[mid] >= target, so mid is a candidate. Any valid answer must be <= mid.
                right = mid;
            }
        }
        return left;
    }

    /**
     * Upper Bound.
     * Finds the first index `i` such that nums[i] > target.
     *
     * @param nums   The sorted input array.
     * @param target The value to search for.
     * @return The first index where nums[index] > target. Returns nums.length if all elements are <= target.
     */
    public int upperBound(int[] nums, int target) {
        int left = 0;
        int right = nums.length; // Search space: [left, right)

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] <= target) {
                // mid is smaller or equal, so answer must be strictly greater than mid
                left = mid + 1;
            } else {
                // nums[mid] > target, so mid is a candidate.
                right = mid;
            }
        }
        return left;
    }

    public static void main(String[] args) {
        BinarySearch bs = new BinarySearch();
        int[] nums = {1, 2, 4, 4, 4, 6, 7};

        System.out.println("Array: " + Arrays.toString(nums));

        // Test search
        System.out.println("\n--- Testing search ---");
        System.out.println("Search 4 (expect index 2, 3, or 4): " + bs.search(nums, 4));
        System.out.println("Search 6 (expect 5): " + bs.search(nums, 6));
        System.out.println("Search 3 (expect -1): " + bs.search(nums, 3));

        // Test lowerBound (first index >= target)
        System.out.println("\n--- Testing lowerBound ---");
        System.out.println("Array: " + Arrays.toString(nums));
        System.out.println("lowerBound 4 (expect 2): " + bs.lowerBound(nums, 4));
        System.out.println("lowerBound 2 (expect 1): " + bs.lowerBound(nums, 2));
        System.out.println("lowerBound 5 (expect 5): " + bs.lowerBound(nums, 5));
        System.out.println("lowerBound 0 (expect 0): " + bs.lowerBound(nums, 0));
        System.out.println("lowerBound 8 (expect 7): " + bs.lowerBound(nums, 8));

        // Test upperBound (first index > target)
        System.out.println("\n--- Testing upperBound ---");
        System.out.println("Array: " + Arrays.toString(nums));
        System.out.println("upperBound 4 (expect 5): " + bs.upperBound(nums, 4));
        System.out.println("upperBound 2 (expect 2): " + bs.upperBound(nums, 2));
        System.out.println("upperBound 5 (expect 6): " + bs.upperBound(nums, 5));
        System.out.println("upperBound 7 (expect 7): " + bs.upperBound(nums, 7));
    }
}
