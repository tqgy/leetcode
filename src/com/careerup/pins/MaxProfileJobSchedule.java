package com.careerup.pinterest;

import java.util.Arrays;
import java.util.TreeMap;

/**
 * Maximum Profit Job Scheduling
 *
 * Problem: Given arrays of start times, end times and associated profits for n
 * jobs, choose a subset of non-overlapping jobs that maximizes total profit.
 * A job that ends at time X allows another job starting at time X.
 *
 * Approach (Greedy + DP via TreeMap):
 *  - Create an array of jobs [start, end, profit] and sort by end time.
 *  - Maintain a TreeMap<endTime, maxProfitUntilEnd> where the value at a key
 *    represents the maximum profit achievable using jobs that finish at or
 *    before that time.
 *  - For each job (in increasing end time): find the largest endTime <= job.start
 *    (TreeMap.floorKey), get its profit, and consider taking the current job.
 *  - Update the global maximum and store it in the map for the current job's end
 *    time only if it improves the recorded profit.
 */
public class MaxProfileJobSchedule {

    /**
     * Compute the maximum profit obtainable by scheduling non-overlapping jobs.
     *
     * @param startTime start times of jobs
     * @param endTime   end times of jobs
     * @param profit    profit of each job
     * @return maximum total profit
     */
    public int jobScheduling(int[] startTime, int[] endTime, int[] profit) {
        // job[start, end, profit]
        int[][] jobs = new int[profit.length][3];
        for(int i = 0; i < jobs.length; i++){
            jobs[i] = new int[]{startTime[i], endTime[i], profit[i]};
        }
        // sort by endTime
        Arrays.sort(jobs, (a, b) -> a[1] - b[1]);
        // sort and cache [endTime, profit]
        TreeMap<Integer, Integer> dp = new TreeMap<>();
        int res = 0;
        for(int[] job : jobs){
            // nearest endTime before current startTime
            Integer preEndIdx = dp.floorKey(job[0]);
            int preProfit = preEndIdx == null ? 0 : dp.get(preEndIdx);
            res = Math.max(res, preProfit + job[2]);
            dp.put(job[1], res);
        }
        return res;
    }

    private static void runTest(int[] s, int[] e, int[] p, int expected, String name) {
        MaxProfileJobSchedule solver = new MaxProfileJobSchedule();
        int res = solver.jobScheduling(s, e, p);
        if (res == expected) {
            System.out.println("PASS: " + name + " -> " + res);
        } else {
            System.err.println("FAIL: " + name + " -> expected " + expected + ", got " + res);
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        // Examples from LeetCode 1235
        runTest(new int[]{1,2,3,3}, new int[]{3,4,5,6}, new int[]{50,10,40,70}, 120, "Example1");
        runTest(new int[]{1,2,3,4,6}, new int[]{3,5,10,6,9}, new int[]{20,20,100,70,60}, 150, "Example2");
        runTest(new int[]{1,1,1}, new int[]{2,3,4}, new int[]{5,6,4}, 6, "Example3");

        // Boundary and additional tests
        runTest(new int[]{1,3,3}, new int[]{3,5,6}, new int[]{5,1,8}, 13, "BoundaryStartAtEnd");
        runTest(new int[]{4,2,4,8,2}, new int[]{5,5,5,10,8}, new int[]{1,2,8,10,4}, 18, "Custom1");

        System.out.println("All tests passed ✅");
    }
}
