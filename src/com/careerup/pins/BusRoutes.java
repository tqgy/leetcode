package com.careerup.pins;

import java.util.*;

/**
 * Bus Routes (LeetCode 815)
 *
 * Given an array of bus routes where routes[i] is a list of stops that the i-th
 * bus visits in a loop, determine the minimum number of buses one must take to
 * travel from stop `source` to stop `target`. Return -1 if impossible.
 *
 * Approach (BFS on buses):
 *  - Build a mapping from stop -> set of buses that visit the stop.
 *  - Start BFS from all buses that include `source`. Each BFS level represents
 *    taking one more bus. When any visited bus reaches `target`, return the
 *    current number of buses taken.
 *
 * Complexity: Let R = total number of routes (buses) and S = total number of
 * stops across all routes. Building the map is O(S). BFS processes each bus
 * and stop at most once: O(S + R).
 */
public class BusRoutes {

    /**
     * Compute the minimum number of buses to take from source to target.
     *
     * @param routes array of routes where routes[i] is the list of stops for bus i
     * @param source starting stop
     * @param target destination stop
     * @return minimum number of buses required, or -1 if unreachable
     */
    public int numBusesToDestination(int[][] routes, int source, int target) {
        // Map each stop to the set of buses that visit it
        Map<Integer, Set<Integer>> stopToBuses = new HashMap<>();
        for (int bus = 0; bus < routes.length; bus++) {
            for (int stop : routes[bus]) {
                stopToBuses.computeIfAbsent(stop, k -> new HashSet<>()).add(bus);
            }
        }

        // Trivial case: already at target
        if (source == target) return 0;

        // If either source or target has no buses, impossible
        if (!stopToBuses.containsKey(source) || !stopToBuses.containsKey(target)) return -1;

        // BFS queue stores bus indices; visited sets prevent revisiting
        Queue<Integer> queue = new LinkedList<>();
        boolean[] visitedBus = new boolean[routes.length];
        Set<Integer> visitedStop = new HashSet<>(); // avoid reprocessing same stops

        // Initialize BFS with all buses that serve the source stop
        for (int bus : stopToBuses.get(source)) {
            queue.offer(bus);
            visitedBus[bus] = true;
        }

        int busesTaken = 0;
        while (!queue.isEmpty()) {
            busesTaken++;
            // Important: need get the size first, the queue size will change in the loop
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int bus = queue.poll();

                // Explore all stops served by this bus
                for (int stop : routes[bus]) {
                    if (stop == target) return busesTaken;

                    // For each unvisited stop, enqueue all unvisited buses that serve it
                    if (!visitedStop.contains(stop)) {
                        visitedStop.add(stop);
                        // getOrDefault to avoid null pointer
                        for (int nextBus : stopToBuses.getOrDefault(stop, Collections.emptySet())) {
                            if (!visitedBus[nextBus]) {
                                visitedBus[nextBus] = true;
                                queue.offer(nextBus);
                            }
                        }
                    }
                }
            }
        }

        return -1; // unreachable
    }

    // ------------------------ Simple in-file tests ---------------------------
    private static void runTest(int[][] routes, int source, int target, int expected, String name) {
        BusRoutes solver = new BusRoutes();
        int res = solver.numBusesToDestination(routes, source, target);
        if (res == expected) {
            System.out.println("PASS: " + name + " -> " + res);
        } else {
            System.err.println("FAIL: " + name + " -> expected " + expected + ", got " + res);
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        // LeetCode example 1
        runTest(new int[][]{ {1,2,7}, {3,6,7} }, 1, 6, 2, "LeetCodeExample1");

        // Direct single-bus route
        runTest(new int[][]{ {1,2,3} }, 1, 3, 1, "DirectRoute");

        // Source equals target
        runTest(new int[][]{ {1,2,7}, {3,6,7} }, 1, 1, 0, "SourceIsTarget");

        // No possible connection
        runTest(new int[][]{ {1,2}, {3,4} }, 1, 4, -1, "NoConnection");

        // Longer chain of transfers
        runTest(new int[][]{ {1,2,3}, {3,4,5}, {5,6,7} }, 1, 7, 3, "ChainTransfers");

        // Repeated stops across routes
        runTest(new int[][]{ {1,2,3}, {2,8}, {8,9,3} }, 1, 9, 2, "RepeatedStops");

        System.out.println("All tests passed ✅");
    }
}
