package com.careerup.pins;

import java.util.*;

/**
 * Problem Description:
 * --------------------
 * AAA arrives at a bank at time 0. There are:
 *   - N agents, numbered 1..N
 *   - Each agent i takes times[i] minutes to serve ONE customer
 *   - All agents are idle at time 0
 *
 * Before AAA, there are M customers already waiting in line.
 * AAA is the (M+1)-th customer.
 *
 * The bank follows strict first-come-first-serve rules:
 *   1) Customers are served in arrival order.
 *   2) If multiple agents are available at the same time,
 *      the customer chooses the agent with the LOWEST agent number.
 *
 * Goal:
 *   Determine how many minutes AAA must wait before she starts being served.
 *
 * Approach:
 *   Use a min-heap (priority queue) of (nextAvailableTime, agentId).
 *   - Initially, all agents are available at time 0.
 *   - For each of the first M customers:
 *       * Pop the earliest-available agent
 *       * Assign the customer
 *       * Push the agent back with updated availability time
 *   - After M customers, the next available agent is the one AAA gets.
 *   - AAA's wait time = that agent's nextAvailableTime.
 *
 * Time Complexity:
 *   O(M log N)
 */

public class BankWaitTime {

    static class Agent {
        int nextAvailableTime; // when this agent becomes free
        int id;                // agent number (1..N)

        Agent(int nextAvailableTime, int id) {
            this.nextAvailableTime = nextAvailableTime;
            this.id = id;
        }
    }

    public static int waitTimeForAAA(int[] times, int M) {
        int N = times.length;

        // Priority queue sorted by:
        //   1) earliest nextAvailableTime
        //   2) lowest agent id (tie-breaker)
        PriorityQueue<Agent> pq = new PriorityQueue<>(
            (a, b) -> a.nextAvailableTime != b.nextAvailableTime
                    ? Integer.compare(a.nextAvailableTime, b.nextAvailableTime)
                    : Integer.compare(a.id, b.id)
        );

        // Step 1: Initialize all agents as available at time 0
        for (int i = 0; i < N; i++) {
            pq.offer(new Agent(0, i + 1)); // agent IDs are 1-based
        }

        // Step 2: Simulate the first M customers
        for (int i = 0; i < M; i++) {
            Agent agent = pq.poll(); // earliest available agent

            // Assign this customer to the agent
            int newTime = agent.nextAvailableTime + times[agent.id - 1];

            // Update agent availability
            pq.offer(new Agent(newTime, agent.id));
        }

        // Step 3: AAA is the next customer
        Agent aaaAgent = pq.poll();

        // AAA waits until this agent becomes free
        return aaaAgent.nextAvailableTime;
    }

    /**
     * Optimized solution for very large M using binary search.
     *
     * Problem:
     *   - N agents, each with service time times[i]
     *   - M customers before AAA
     *   - AAA is the (M+1)-th customer
     *   - All agents start idle at time 0
     *   - Customers always choose the earliest available agent
     *
     * Goal:
     *   Compute how long AAA waits before being served.
     *
     * Approach:
     *   Binary search on time T:
     *     served(T) = sum(T / times[i] + 1)
     *   Find smallest T where served(T) >= M + 1.
     * 
     * Time Complexity: O(N * log(maxTime))
     * Space Complexity: O(1)
     */
    public static long waitTimeBinarySearch(int[] times, long M) {
        long left = 0;
        // long right = (long) 1e18; // safe upper bound
        long minTime = times[0];
        for (int t : times)
            minTime = Math.min(minTime, (long)t);
        // real upper bound
        long right = (M + 1) * minTime;


        while (left < right) {
            long mid = left + (right - left) / 2;

            if (served(times, mid) >= M + 1) {
                right = mid; // mid is enough, try smaller
            } else {
                left = mid + 1; // mid too small
            }
        }

        return left; // earliest time AAA can start service
    }

    // Compute how many customers could have started service by time T
    // Each agent starts at 0, and then every times[i] minutes.
    // Count = 1 (for t=0) + T / times[i]
    private static long served(int[] times, long T) {
        long total = 0;
        for (int t : times) {
            total += (T / t) + 1;
            if (total < 0) 
                return Long.MAX_VALUE; // overflow guard
        }
        return total;
    }

    // Example usage
    public static void main(String[] args) {
        int[] times = {2, 3, 5}; // agent 1 takes 2 min, agent 2 takes 3 min, agent 3 takes 5 min
        int M = 4;               // 4 customers before AAA

        int wait = waitTimeForAAA(times, M);
        System.out.println("AAA waits: " + wait + " minutes");
        long waitBinary = waitTimeBinarySearch(times, M);
        System.out.println("AAA binary waits: " + waitBinary + " minutes");

        // test more test cases for more data in times
        int[] times2 = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29};
        int M2 = 100;
        int wait2 = waitTimeForAAA(times2, M2);
        System.out.println("AAA waits: " + wait2 + " minutes");
        long waitBinary2 = waitTimeBinarySearch(times2, M2);
        System.out.println("AAA binary waits: " + waitBinary2 + " minutes");
    }
}
