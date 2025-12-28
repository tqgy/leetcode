package com.careerup.carrot;

import java.util.*;

/**
 * Solution for the MinShoppers problem. The goal is to find the minimum number
 * of shoppers required to keep the average wait time of orders below a certain
 * threshold k.
 */
class MinShoppers {

    /**
     * Calculates the minimum number of shoppers required.
     * 
     * @param orders A 2D array where each element is {duration, arrival_time}.
     * @param k      The maximum allowed average wait time.
     * @return The minimum number of shoppers, or -1 if even infinite shoppers
     *         cannot satisfy the condition.
     */
    public int getMinShoppers(int[][] orders, double k) {
        if (orders == null || orders.length == 0) {
            return 0;
        }

        // 1. Basic check: Even with infinite shoppers, the wait time for each order is
        // at least its duration.
        // If the average duration exceeds k, it's impossible.
        double totalDuration = 0;
        for (int[] order : orders) {
            totalDuration += order[0];
        }
        if (totalDuration / orders.length > k) {
            return -1;
        }

        // 2. Sort orders.
        // Primary key: Arrival time (earlier first).
        // Secondary key: Duration (shorter first).
        // This is a greedy strategy: satisfying shorter jobs first can reduce total
        // wait time in some scheduling contexts, though for this specific simulation
        // with fixed arrival times, arrival order is dominant.
        // Tie-breaking with shorter duration helps clear the queue faster.
        Arrays.sort(orders, (a, b) -> {
            if (a[1] != b[1]) {
                return Integer.compare(a[1], b[1]);
            } else {
                return Integer.compare(a[0], b[0]);
            }
        });

        // 3. Binary Search for the minimum number of shoppers.
        // Condition: monotonicity. More shoppers -> lower (or equal) average wait time.
        // Range: [1, orders.length]. In worst case, we need one shopper per order
        // (simultaneous arrivals).

        int lo = 1, hi = orders.length;

        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            double avgTime = getAverageWaitTime(orders, mid);
            if (avgTime <= k) {
                hi = mid; // Try fewer shoppers (or this one) to find the minimum
            } else {
                lo = mid + 1; // Need more shoppers
            }
        }

        return lo;
    }

    /**
     * Simulates the process with a fixed number of shoppers and calculates the
     * average wait time.
     * 
     * @param orders   Sorted list of orders.
     * @param shoppers Number of available shoppers.
     * @return Average wait time.
     */
    private double getAverageWaitTime(int[][] orders, int shoppers) {
        // PriorityQueue stores the time when each shopper becomes free.
        PriorityQueue<Integer> freeShoppers = new PriorityQueue<>();

        // Initially, all shoppers are free at time 0. However, to save space/time,
        // we can just add to PQ when they take a job, or initialize with 0s.
        // A cleaner approach for simulation:
        // The PQ size represents currently busy shoppers.
        // If PQ size < shoppers, we have a free shopper immediately.
        // If PQ size == shoppers, we must wait for the one who finishes earliest.

        long totalWait = 0; // Use long to prevent overflow

        // We can just track the finish times of the *busy* shoppers.
        // But simpler logic is: maintain a pool of 'finish times' for all 'shoppers'
        // slots.
        // Pre-fill with 0s for all shoppers.
        for (int i = 0; i < shoppers; i++) {
            freeShoppers.offer(0);
        }

        for (int[] order : orders) {
            int duration = order[0];
            int arrive = order[1];

            // Get the earliest time a shopper is free
            int earliestFreeTime = freeShoppers.poll();

            // The shopper can start either when they become free or when the order arrives,
            // whichever is later.
            int start = Math.max(earliestFreeTime, arrive);

            int finish = start + duration;

            // Wait time = (finish time) - (arrival time)
            totalWait += (finish - arrive);

            // This shopper is now busy until 'finish'
            freeShoppers.offer(finish);
        }

        return (double) totalWait / orders.length;
    }

    // Kept for reference but not part of the core logic for getMinShoppers
    public double averageWaitingTime(int[][] customers) {
        if (customers == null || customers.length == 0)
            return 0.0;

        // Sort by arrival time to ensure correct simulation
        Arrays.sort(customers, (a, b) -> Integer.compare(a[0], b[0]));

        double total = 0;
        int preFinish = 0;
        for (int i = 0; i < customers.length; i++) {
            int[] cur = customers[i];
            int arrive = cur[0];
            int cost = cur[1];
            int start = Math.max(arrive, preFinish);
            preFinish = start + cost;
            total += preFinish - arrive;
        }
        return total / customers.length;
    }

    public static void main(String[] args) {
        MinShoppers solver = new MinShoppers();

        // Test Case 1: Simple case
        int[][] orders1 = { { 3, 0 }, { 2, 1 }, { 5, 3 } };
        double k1 = 3.0; // Average duration is (3+2+5)/3 = 3.33 > 3? No wait.
        // Durations: 3, 2, 5. Arrive: 0, 1, 3.
        // 1 shopper:
        // Order 1 (3,0) -> starts 0, ends 3. Wait: 3.
        // Order 2 (2,1) -> starts 3, ends 5. Wait: 5-1=4.
        // Order 3 (5,3) -> starts 5, ends 10. Wait: 10-3=7.
        // Total wait: 3+4+7 = 14. Avg: 4.66 > 3.0.
        // 2 shoppers:
        // Order 1 (3,0) -> starts 0, ends 3. Shopper A free at 3.
        // Order 2 (2,1) -> starts 1, ends 3. Shopper B free at 3.
        // Order 3 (5,3) -> starts 3, ends 8. Shopper A/B free at 3.
        // Wait: (3-0) + (3-1) + (8-3) = 3 + 2 + 5 = 10. Avg: 3.33 > 3.0?
        // Wait... Avg duration is 3.33. Min average wait time IS avg duration if no
        // queuing logic adds delay.
        // Wait time = duration + queue_time.
        // Avg Wait = Avg Duration + Avg Queue.
        // Avg Duration = (3+2+5)/3 = 3.333.
        // Since 3.333 > k=3.0, it should return -1.
        System.out.println("Test Case 1: " + solver.getMinShoppers(orders1, k1) + " (Expected: -1)");

        // Test Case 2:
        int[][] orders2 = { { 10, 0 }, { 10, 2 }, { 10, 4 }, { 10, 6 } };
        double k2 = 15.0;
        System.out.println("Test Case 2: " + solver.getMinShoppers(orders2, k2) + " (Expected: 2)");
        // Details: Avg wait for 1 shopper will be high.
        // 1 shopper: Finishes at 10, 20, 30, 40.
        // Waits: 10, 18, 26, 34 -> Avg 22.
        // 2 shoppers:
        // S1: 0-10, S2: 2-12.
        // S1: 10-20 (take order 3 arr 4), S2: 12-22 (take order 4 arr 6).
        // Waits: (10-0)=10, (12-2)=10, (20-4)=16, (22-6)=16. Sum=52. Avg=13 <= 15.
        // Matches.

        // Test Case 3: Impossible case
        int[][] orders3 = { { 5, 0 }, { 5, 1 }, { 5, 2 }, { 5, 3 } };
        double k3 = 4.0;
        // Avg duration 5. > 4. Impossible.
        System.out.println("Test Case 3: " + solver.getMinShoppers(orders3, k3) + " (Expected: -1)");

        // Test Case 4: Single order
        int[][] orders4 = { { 5, 10 } };
        double k4 = 5.0;
        System.out.println("Test Case 4: " + solver.getMinShoppers(orders4, k4) + " (Expected: 1)");

        // Test Case 5: Exact K
        int[][] orders5 = { { 2, 0 }, { 2, 0 } };
        double k5 = 2.0;
        // 1 shopper: 0->2 (wait 2), 2->4 (wait 4). Avg 3.
        // 2 shoppers: 0->2, 0->2. Avg 2.
        System.out.println("Test Case 5: " + solver.getMinShoppers(orders5, k5) + " (Expected: 2)");

        // test averageWaitingTime with unsorted input
        // {5,2} arrives at 5, takes 2. {1,2} arrives at 1, takes 2. {4,3} arrives at
        // 4, takes 3.
        // Sorted: {1,2}, {4,3}, {5,2}
        // {1,2} -> start 1, end 3. Wait 3-1=2.
        // {4,3} -> start 4 (since 4>3), end 7. Wait 7-4=3.
        // {5,2} -> start 7 (since 7>5), end 9. Wait 9-5=4.
        // Total wait: 2+3+4 = 9. Avg: 9/3 = 3.0.
        // If unsorted (processed as {5,2}, {1,2}, {4,3}):
        // {5,2} -> start 5, end 7. Wait 2.
        // {1,2} -> start 7, end 9. Wait 8.
        // {4,3} -> start 9, end 12. Wait 8.
        // Total wait 18. Avg 6.0.
        int[][] customers = { { 5, 2 }, { 1, 2 }, { 4, 3 } };
        System.out.println("Test Case 6 (averageWaitingTime unsorted): " + solver.averageWaitingTime(customers)
                + " (Expected: 3.0)");

        System.out.println("All tests executed.");
    }
}