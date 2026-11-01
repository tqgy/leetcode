package com.careerup.pins;

/**
 * In this problem, you are provided with a binary matrix named image of
 * dimensions m x n where each entry is either 0 or 1. Here, 0 indicates a white
 * pixel, whereas 1 indicates a black pixel. All black pixels present in the
 * matrix form a singular connected region, connected by their edges (not
 * diagonally).
 * 
 * Given a specific pixel location (x, y) within this matrix which is black
 * (image[x][y] == '1'), your task is to compute the area (in terms of the
 * number of pixels) of the smallest rectangle that can encompass all the black
 * pixels entirely. This rectangle must be aligned with the axes of the matrix.
 * 
 * Additionally, the solution you devise should strive for an efficiency better
 * than O(mn) in terms of runtime complexity.
 */
public class SmallestRectangleContain1 {

    static int minRow, maxRow, minCol, maxCol;
    static int numRows, numCols;

    /**
     * Calculates the minimum area of the rectangle containing all black pixels using Depth First Search (DFS).
     * This method traverses the connected black pixels starting from the given coordinates (x, y)
     * and updates the boundaries (min/max row and col) to encompass all visited black pixels.
     * 
     * Time Complexity: O(R * C) where R is rows and C is columns, as it may visit all pixels in the worst case.
     * Space Complexity: O(R * C) for the recursion stack in the worst case (e.g., snake-like pattern).
     *
     * @param image The binary matrix representing the image.
     * @param x     The starting row index of a black pixel.
     * @param y     The starting column index of a black pixel.
     * @return The area of the smallest rectangle containing all black pixels.
     */
    public static int minimumAreaDFS(char[][] image, int x, int y) {
        numRows = image.length;
        numCols = image[0].length;

        minRow = maxRow = x;
        minCol = maxCol = y;

        dfs(image, x, y);

        // DFS uses [inclusive, inclusive] bounds: both min and max are actual indices with black pixels
        return (maxRow - minRow + 1) * (maxCol - minCol + 1);
    }

    private static void dfs(char[][] image, int r, int c) {
        // Boundary check, visited check, or white pixel check
        if (r < 0 || r >= numRows || c < 0 || c >= numCols || image[r][c] != '1') {
            return;
        }

        // Mark as visited by changing '1' (black) to '0' (white)
        image[r][c] = '0';

        // Update the bounding box of the rectangle
        minRow = Math.min(minRow, r);
        maxRow = Math.max(maxRow, r);
        minCol = Math.min(minCol, c);
        maxCol = Math.max(maxCol, c);

        // Explore 4 directions: down, up, right, left
        dfs(image, r + 1, c);
        dfs(image, r - 1, c);
        dfs(image, r, c + 1);
        dfs(image, r, c - 1);
    }

    /**
     * Calculates the minimum area of the rectangle containing all black pixels using Binary Search.
     * This approach is optimized for cases where O(mn) is too slow. It exploits the property that
     * black pixels are connected to find the boundaries independently.
     * 
     * The strategy is to find:
     * 1. Top-most row with a black pixel.
     * 2. Bottom-most row with a black pixel.
     * 3. Left-most column with a black pixel.
     * 4. Right-most column with a black pixel.
     * 
     * Time Complexity: O(M * log N + N * log M) where M is rows and N is columns.
     * Space Complexity: O(1).
     *
     * @param grid The binary matrix.
     * @param x    The starting row index of a black pixel.
     * @param y    The starting column index of a black pixel.
     * @return The area of the smallest rectangle.
     */
    public static int minimumArea(char[][] grid, int x, int y) {
        int rows = grid.length;
        int cols = grid[0].length;
        
        // Find the left boundary: leftmost column with a black pixel
        int minCol = findLeftBoundary(grid, 0, y, 0, rows);
        
        // Find the right boundary: rightmost column with a black pixel
        int maxCol = findRightBoundary(grid, y, cols - 1, 0, rows);
        
        // Find the top boundary: topmost row with a black pixel
        int minRow = findTopBoundary(grid, 0, x, minCol, maxCol + 1);
        
        // Find the bottom boundary: bottommost row with a black pixel
        int maxRow = findBottomBoundary(grid, x, rows - 1, minCol, maxCol + 1);
        
        // Both boundaries are inclusive, so add 1 to get dimensions
        return (maxCol - minCol + 1) * (maxRow - minRow + 1);
    }

    // Helper: Check if a column contains any black pixel in the given row range
    private static boolean columnHasBlack(char[][] grid, int col, int topRow, int bottomRow) {
        for (int r = topRow; r < bottomRow; r++) {
            if (grid[r][col] == '1') {
                return true;
            }
        }
        return false;
    }

    // Helper: Check if a row contains any black pixel in the given column range
    private static boolean rowHasBlack(char[][] grid, int row, int leftCol, int rightCol) {
        for (int c = leftCol; c < rightCol; c++) {
            if (grid[row][c] == '1') {
                return true;
            }
        }
        return false;
    }

    // Find the leftmost column with a black pixel
    private static int findLeftBoundary(char[][] grid, int left, int right, int topRow, int bottomRow) {
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (columnHasBlack(grid, mid, topRow, bottomRow)) {
                right = mid; // Answer is at mid or to the left
            } else {
                left = mid + 1; // Answer must be to the right
            }
        }
        return left;
    }

    // Find the rightmost column with a black pixel
    private static int findRightBoundary(char[][] grid, int left, int right, int topRow, int bottomRow) {
        while (left < right) {
            int mid = left + (right - left + 1) / 2; // Use upper mid for right boundary
            if (columnHasBlack(grid, mid, topRow, bottomRow)) {
                left = mid; // Answer is at mid or to the right
            } else {
                right = mid - 1; // Answer must be to the left
            }
        }
        return left;
    }

    // Find the topmost row with a black pixel
    private static int findTopBoundary(char[][] grid, int top, int bottom, int leftCol, int rightCol) {
        while (top < bottom) {
            int mid = top + (bottom - top) / 2;
            if (rowHasBlack(grid, mid, leftCol, rightCol)) {
                bottom = mid; // Answer is at mid or above
            } else {
                top = mid + 1; // Answer must be below
            }
        }
        return top;
    }

    // Find the bottommost row with a black pixel
    private static int findBottomBoundary(char[][] grid, int top, int bottom, int leftCol, int rightCol) {
        while (top < bottom) {
            int mid = top + (bottom - top + 1) / 2; // Use upper mid for bottom boundary
            if (rowHasBlack(grid, mid, leftCol, rightCol)) {
                top = mid; // Answer is at mid or below
            } else {
                bottom = mid - 1; // Answer must be above
            }
        }
        return top;
    }

    public static void main(String[] args) {
        char[][] grid = { { '0', '0', '1', '0' }, { '0', '1', '1', '0' }, { '0', '1', '0', '0' } };
        int x = 0, y = 2;
        System.out.println("Binary Search Result: " + minimumArea(grid, x, y)); // Expected: 6
        
        // Note: DFS modifies the grid (marks visited), so we need to reset or use a clone for a fair test if run sequentially with the same object.
        // However, in this specific example, minimumArea (BS) does NOT modify the grid.
        // minimumAreaDFS DOES modify the grid. So we run BS first.
        System.out.println("DFS Result: " + minimumAreaDFS(grid, x, y)); // Expected: 6
    }
}
