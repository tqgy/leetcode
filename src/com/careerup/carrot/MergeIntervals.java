package com.careerup.carrot;

import java.util.*;

/**
 * Merging a list of intervals (LC56)
 * Inserting a new interval into an existing list and merging automatically (LC57)
 */
public class MergeIntervals {

    // -------------------------------
    // LeetCode 56: Merge Intervals
    // -------------------------------
    public static int[][] merge(int[][] intervals) {
        if (intervals == null || intervals.length <= 1)
            return intervals;

        // Sort by start time
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        List<int[]> result = new ArrayList<>();
        int[] current = intervals[0];

        for (int i = 1; i < intervals.length; i++) {
            int[] next = intervals[i];

            if (next[0] <= current[1]) {
                // Overlap → merge
                current[1] = Math.max(current[1], next[1]);
            } else {
                result.add(current);
                current = next;
            }
        }

        result.add(current);
        return result.toArray(new int[result.size()][]);
    }

    // -------------------------------
    // LeetCode 57: Insert Interval
    // -------------------------------
    public static int[][] insert(int[][] intervals, int[] newInterval) {
        List<int[]> list = new ArrayList<>();

        int i = 0;
        int n = intervals.length;

        // 1. Add all intervals ending before newInterval starts
        while (i < n && intervals[i][1] < newInterval[0]) {
            list.add(intervals[i]);
            i++;
        }

        // 2. Merge all overlapping intervals with newInterval
        while (i < n && intervals[i][0] <= newInterval[1]) {
            newInterval[0] = Math.min(newInterval[0], intervals[i][0]);
            newInterval[1] = Math.max(newInterval[1], intervals[i][1]);
            i++;
        }
        list.add(newInterval);

        // 3. Add the rest
        while (i < n) {
            list.add(intervals[i]);
            i++;
        }

        return list.toArray(new int[list.size()][]);
    }

    // -------------------------------
    // Combined API: Insert + Merge
    // -------------------------------
    public static int[][] insertAndMerge(int[][] intervals, int[] newInterval) {
        return merge(insert(intervals, newInterval));
    }

    // Example usage
    public static void main(String[] args) {
        int[][] intervals = { { 1, 3 }, { 2, 6 }, { 8, 10 }, { 15, 18 } };
        int[] newInterval = { 4, 9 };

        System.out.println("Merged (LC56): " + Arrays.deepToString(merge(intervals)));
        System.out.println("Insert (LC57): " + Arrays.deepToString(insert(intervals, newInterval)));
        System.out.println("Insert+Merge: " + Arrays.deepToString(insertAndMerge(intervals, newInterval)));
    }
}
