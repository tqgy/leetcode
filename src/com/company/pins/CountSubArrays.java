package com.careerup.pins;

import java.util.*;

/**
 * CountSubArrays: count number of non-empty subarrays whose "score" < k.
 *
 * The score of a subarray is defined as (sum of elements) * (length).
 * This class implements a linear two-pointer (sliding window) solution and
 * provides a brute-force validator plus tests in {@code main()}.
 *
 * Algorithm (sliding window): maintain window [left, right] and its sum.
 * For each right, expand the window by adding nums[right], then shrink left
 * while (sum * windowLength) >= k. All subarrays ending at right with
 * starting index in [left..right] have score < k, so add (right - left + 1)
 * to the answer. This runs in O(n) time and O(1) extra space.
 */
public class CountSubArrays {

    /**
     * Count number of non-empty subarrays whose score (sum * length) is strictly less than k.
     *
     * Time: O(n) where n = nums.length. Space: O(1) extra.
     *
     * @param nums positive integer array
     * @param k threshold (strictly less than)
     * @return number of subarrays with score < k
     */
    public int countSubArrays(int[] nums, int k) {
        int n = nums.length;
        int left = 0;
        int right = 0;
        long currentSum = 0L; // use long to avoid intermediate overflow
        int count = 0;

        while (right < n) {
            currentSum += nums[right];

            // Shrink from the left while the current window's score >= k
            while (left <= right && currentSum * (right - left + 1) >= k) {
                currentSum -= nums[left];
                left++;
            }

            // All subarrays ending at 'right' and starting between left..right are valid
            count += right - left + 1;
            right++;
        }

        return count;
    }

    // ------------------ Brute-force validator and tests ------------------

    /**
     * Brute-force computation used for small inputs to validate correctness.
     */
    public static int bruteForce(int[] nums, int k) {
        int n = nums.length;
        int cnt = 0;
        for (int i = 0; i < n; i++) {
            long sum = 0L;
            for (int j = i; j < n; j++) {
                sum += nums[j];
                if (sum * (j - i + 1) < k) cnt++;
            }
        }
        return cnt;
    }

    private static void runTest(String name, int[] nums, int k) {
        CountSubArrays solver = new CountSubArrays();
        int got = solver.countSubArrays(nums, k);
        int expected = bruteForce(nums, k);
        System.out.println(String.format("%s: nums=%s k=%d => expected=%d got=%d %b",
                name, Arrays.toString(nums), k, expected, got, expected == got));
    }

    public static void main(String[] args) {
        // Deterministic tests
        runTest("Simple1", new int[]{1, 2, 3, 4, 5}, 75);
        runTest("SmallK", new int[]{1, 1, 1}, 2);
        runTest("SingleElem", new int[]{5}, 6);
        runTest("SingleElemFail", new int[]{5}, 5);
        runTest("Zeros", new int[]{1, 1, 1, 1}, 100);

        // Increasing sizes
        runTest("Increasing", new int[]{1, 2, 3, 4}, 20);

        // Cases with larger values
        runTest("LargeVals", new int[]{10, 20, 30}, 500);

        // Randomized small tests to validate against brute force
        Random rnd = new Random(42);
        for (int t = 0; t < 10; t++) {
            int n = 1 + rnd.nextInt(8);
            int[] nums = new int[n];
            for (int i = 0; i < n; i++) nums[i] = 1 + rnd.nextInt(10);
            int k = 1 + rnd.nextInt(200);
            runTest("Random" + t, nums, k);
        }
    }

}

