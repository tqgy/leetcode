package com.careerup.pins;

import java.util.*;

/**
 * Problem: You are given a 2D grid that contains mirrors and empty cells. Each
 * cell can be: '.' - empty '/' - 45-degree mirror '\' - 135-degree mirror
 *
 * A light ray starts at position (sx, sy) with an initial direction dir. The
 * direction is one of: 0 = UP 1 = RIGHT 2 = DOWN 3 = LEFT
 *
 * At each step: - The ray moves one cell in its current direction. - If it hits
 * a mirror, its direction changes according to reflection rules. - If the next
 * move would go out of the grid boundary, the ray reflects off the boundary:
 * its direction is reversed (dir = (dir + 2) % 4), and it does not move in that
 * step (only the direction changes).
 *
 * Question: Determine whether the ray can eventually reach the target cell (tx,
 * ty). If it can reach, return true; otherwise, return false.
 *
 * Important: The ray can get into a loop. To avoid infinite loops, we must
 * detect whether a state (x, y, dir) is visited more than once. If we revisit
 * the same (x, y, dir), we will never reach the target and should return false.
 */
public class MirrorRoom {

    // Directions: 0 = UP, 1 = RIGHT, 2 = DOWN, 3 = LEFT
    // dx, dy arrays represent movement in each direction.
    private static final int[] DX = { -1, 0, 1, 0 };
    private static final int[] DY = { 0, 1, 0, -1 };

    /**
     * Check if the ray starting from (sx, sy) with direction dir can reach (tx, ty)
     * on the given grid.
     */
    public boolean canReach(char[][] grid, int sx, int sy, int dir, int tx, int ty) {
        int m = grid.length;
        int n = grid[0].length;

        // visited[x][y][dir] = true means we have already seen
        // this position with this direction. If we see it again,
        // it implies an infinite loop.
        boolean[][][] visited = new boolean[m][n][4];

        int x = sx;
        int y = sy;

        while (true) {
            // Check if we reached the target
            if (x == tx && y == ty) {
                return true;
            }

            // If this state is visited again, we are in a loop
            if (visited[x][y][dir]) {
                return false;
            }
            visited[x][y][dir] = true;

            // Compute next position
            int nx = x + DX[dir];
            int ny = y + DY[dir];

            // Boundary reflection:
            // If the next cell is outside the grid, we only change direction
            // (reverse it) and stay in the same position.
            if (nx < 0 || nx >= m || ny < 0 || ny >= n) {
                // Reverse direction (UP <-> DOWN, LEFT <-> RIGHT)
                dir = (dir + 2) % 4;
                // Do not move, just continue with the new direction
                continue;
            }

            // Move into the next cell
            x = nx;
            y = ny;

            // Mirror reflection rules

            // '/' mirror (45-degree mirror)
            // Mapping:
            // UP (0) -> RIGHT (1)
            // RIGHT (1) -> UP (0)
            // DOWN (2) -> LEFT (3)
            // LEFT (3) -> DOWN (2)
            if (grid[x][y] == '/') {
                if (dir == 0) {
                    dir = 1;
                } else if (dir == 1) {
                    dir = 0;
                } else if (dir == 2) {
                    dir = 3;
                } else { // dir == 3
                    dir = 2;
                }
            }
            // '\' mirror (135-degree mirror)
            // Mapping:
            // UP (0) -> LEFT (3)
            // LEFT (3) -> UP (0)
            // DOWN (2) -> RIGHT (1)
            // RIGHT (1) -> DOWN (2)
            else if (grid[x][y] == '\\') {
                if (dir == 0) {
                    dir = 3;
                } else if (dir == 3) {
                    dir = 0;
                } else if (dir == 2) {
                    dir = 1;
                } else { // dir == 1
                    dir = 2;
                }
            }
        }
    }

    // Simple example main to illustrate usage
    public static void main(String[] args) {
        MirrorRoom sol = new MirrorRoom();

        char[][] grid = { { '.', '/', '.' }, { '.', '.', '\\' }, { '.', '.', '.' } };

        int sx = 2, sy = 0; // start at bottom-left
        int tx = 0, ty = 2; // target at top-right
        int dir = 1; // initial direction: RIGHT

        boolean canReach = sol.canReach(grid, sx, sy, dir, tx, ty);
        System.out.println("Can reach target: " + canReach);
    }
}
