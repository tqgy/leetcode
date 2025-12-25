package com.careerup.instacart;

import java.util.*;

class MinShoppers {

    public int getMinShoppers(int[][] orders, double k) {
        if (orders.length == 0) {
            return 0;
        }

        int totalDuration = 0;
        for (int[] order : orders) {
            totalDuration += order[0];
        }
        if (((double) totalDuration) / orders.length > k) {
            return -1;
        }

        // Sort orders by the time they are received, if received time is the same, we want short order be processed first
        Arrays.sort(orders, (a, b) -> {
            if (a[1] == b[1]) {
                return a[0] - b[0];
            } else {
                return a[1] - b[1];
            }
        });

        // Binary search to find the minimum number of shoppers
        int lo = 1, hi = orders.length;
        while (lo < hi) {
            int mid = (lo + hi) / 2;
            double avgTime = getAverageWaitTime(orders, mid);
            if (avgTime <= k) {
                hi = mid;
            } else {
                lo = mid + 1;
            }
        }

        return lo;
    }

    private double getAverageWaitTime(int[][] orders, int shoppers) {
        PriorityQueue<Integer> pq = new PriorityQueue<>(); // min heap to store the end time of each shopper
        int totalWait = 0, start = orders[0][1];

        for (int i = 0; i < orders.length; i++) {
            int duration = orders[i][0];
            int arrive = orders[i][1];

            // Free up shoppers who have finished their orders before the current order arrives
            while (!pq.isEmpty() && pq.peek() <= arrive) {
                pq.poll();
            }

            if (pq.size() == shoppers) {
                start = pq.poll(); // Poll the earliest finished order, then we can start proceed the next order immediately
            }

            // If we have finished all the orders, we can start the next order immediately
            start = Math.max(start, arrive);

            int finished = start + duration;
            pq.offer(finished);
            totalWait += (finished - arrive);
        }

        return ((double) totalWait) / orders.length;
    }
}