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

    public double averageWaitingTime(int[][] customers) {
        if(customers == null || customers.length == 0 || customers[0].length == 0) 
            return 0.0;
        double total = 0;
        int preFinish = 0;
        for(int i = 0; i < customers.length; i++){
            int[] cur = customers[i];
            int arrive = cur[0];
            int cost = cur[1];
            int start = Math.max(arrive, preFinish);
            preFinish = start + cost;
            total += preFinish - arrive;
        }
        return total/customers.length;
    }

    public static void main(String[] args) {
        MinShoppers solver = new MinShoppers();

        int[][] orders1 = { {3, 0}, {2, 1}, {5, 3} };
        double k1 = 3.0;
        System.out.println(solver.getMinShoppers(orders1, k1)); // Expected output: 2

        int[][] orders2 = { {10, 0}, {10, 2}, {10, 4}, {10, 6} };
        double k2 = 15.0;
        System.out.println(solver.getMinShoppers(orders2, k2)); // Expected output: 2

        int[][] orders3 = { {5, 0}, {5, 1}, {5, 2}, {5, 3} };
        double k3 = 4.0;
        System.out.println(solver.getMinShoppers(orders3, k3)); // Expected output: -1

        // test averageWaitingTime
        int[][] customers = {{1,2},{2,5},{4,3}};
        System.out.println(solver.averageWaitingTime(customers)); // Expected output: 5.0
    }
}