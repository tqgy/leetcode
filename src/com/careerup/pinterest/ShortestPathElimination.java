package com.careerup.pinterest;

import java.util.*;

/**
 * Shortest path with obstacle elimination (LeetCode 1293 style).
 *
 * Given an m x n grid where 0 represents an empty cell and 1 an obstacle,
 * you can move in four directions. You may eliminate at most k obstacles.
 * This class provides a BFS-based solution that tracks remaining eliminations
 * per state using a 3D visited array: visited[x][y][remainingK].
 *
 * Time complexity: O(m * n * k) in the worst case because each state (x,y,remainingK)
 * is visited at most once. Space complexity: O(m * n * k).
 */
public class ShortestPathElimination {
    /**
     * Return minimum number of steps to get from (0,0) to (m-1,n-1) given up to k
     * obstacle eliminations, or -1 if no such path exists.
     *
     * @param grid m x n grid of 0/1
     * @param k maximum obstacles you may eliminate
     * @return minimum steps or -1
     */
    public int shortestPath(int[][] grid, int k) {
        if (grid == null || grid.length == 0 || grid[0].length == 0) return -1;
        int m = grid.length;
        int n = grid[0].length;

        // If k is large enough, the shortest path is simply Manhattan distance
        int minStepsIfNoObstacles = m - 1 + n - 1;
        if (k >= minStepsIfNoObstacles) return minStepsIfNoObstacles;

        // visited[x][y][remainingK] prevents revisiting an identical state
        boolean[][][] visited = new boolean[m][n][k + 1];
        visited[0][0][k] = true;

        // Directions: right, down, left, up
        int[][] directions = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };

        // BFS queue stores {x, y, remainingK}
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[] { 0, 0, k });

        int steps = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int[] curr = queue.poll();
                int x = curr[0], y = curr[1], remainingK = curr[2];

                // reached destination
                if (x == m - 1 && y == n - 1) {
                    return steps;
                }

                // explore neighbors
                for (int[] dir : directions) {
                    int nx = x + dir[0];
                    int ny = y + dir[1];
                    if (nx < 0 || nx >= m || ny < 0 || ny >= n) continue;

                    int nk = remainingK - grid[nx][ny];
                    if (nk >= 0 && !visited[nx][ny][nk]) {
                        visited[nx][ny][nk] = true;
                        queue.offer(new int[] { nx, ny, nk });
                    }
                }
            }
            steps++;
        }

        return -1; // not reachable
    }

    // ------------------ Test harness ------------------
    private static void runTest(String name, int[][] grid, int k, int expected) {
        ShortestPathElimination s = new ShortestPathElimination();
        int got = s.shortestPath(grid, k);
        System.out.println(String.format("%s: k=%d expected=%d got=%d => %b", name, k, expected, got, got == expected));
    }

    public static void main(String[] args) {
        // Trivial: single cell
        runTest("SingleCell", new int[][] { { 0 } }, 0, 0);

        // Simple clear 2x2 grid: (0,0)->(1,1) in 2 steps
        runTest("Clear2x2", new int[][] { { 0, 0 }, { 0, 0 } }, 0, 2);

        // Blocked 2x2: both neighbors are obstacles
        runTest("Blocked2x2_k1", new int[][] { { 0, 1 }, { 1, 0 } }, 0, -1);
        runTest("Blocked2x2_k2", new int[][] { { 0, 1 }, { 1, 0 } }, 1, 2);

        // Known leetcode style case: small obstacle in middle
        int[][] g3 = { { 0, 0, 0 }, { 1, 1, 0 }, { 0, 0, 0 } };
        runTest("LeetExample_k1", g3, 1, 4);
        runTest("LeetExample_k0", g3, 0, 4);

        // Dense obstacles but enough k to take Manhattan path
        int[][] g4 = { { 0, 1, 1 }, { 1, 1, 1 }, { 1, 1, 0 } };
        runTest("Dense_k4", g4, 4, 4);

        // Impossible: ring of obstacles enclosing target
        int[][] g5 = { { 0, 1, 0 }, { 1, 1, 1 }, { 0, 1, 0 } };
        runTest("Ring_k1", g5, 1, -1);
        runTest("Ring_k2", g5, 2, 4);
    }
}
