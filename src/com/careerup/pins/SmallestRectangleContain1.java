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
        
        // Find the left boundary: binary search in range [0, y]
        int minCol = searchColumnBoundary(grid, 0, y, 0, rows, true);
        
        // Find the right boundary: binary search in range [y + 1, cols]
        int maxCol = searchColumnBoundary(grid, y + 1, cols, 0, rows, false);
        
        // Find the top boundary: binary search in range [0, x], constrained by min/max cols
        int minRow = searchRowBoundary(grid, 0, x, minCol, maxCol, true);
        
        // Find the bottom boundary: binary search in range [x + 1, rows], constrained by min/max cols
        int maxRow = searchRowBoundary(grid, x + 1, rows, minCol, maxCol, false);
        
        return (maxCol - minCol) * (maxRow - minRow);
    }

    /**
     * Binary search to find the column boundary.
     * 
     * @param grid          The image matrix.
     * @param start         Start index of column range (inclusive).
     * @param end           End index of column range (exclusive).
     * @param top           Top row index to scan.
     * @param bottom        Bottom row index to scan (exclusive).
     * @param checkHasBlackPixel If true, we look for the first column that HAS a black pixel (min boundary).
     *                           If false, we look for the first column that has NO black pixels (max boundary).
     * @return The column index establishing the boundary.
     */
    private static int searchColumnBoundary(char[][] grid, int start, int end, int top, int bottom, boolean checkHasBlackPixel) {
        while (start != end) {
            int mid = (start + end) / 2;
            int row = top;
            // Scan the column 'mid' to see if it contains any black pixel ('1')
            while (row < bottom && grid[row][mid] == '0') {
                ++row;
            }
            
            // If checkHasBlackPixel is true: we want the FIRST column with a black pixel.
            //   - If current col has black (row < bottom), then the boundary is mid or to the left. (end = mid)
            //   - If current col is all white, boundary is to the right. (start = mid + 1)
            //
            // If checkHasBlackPixel is false: we want the FIRST column that is ALL WHITE after the black region.
            //   - If current col has black (row < bottom), it's still part of the region, boundary is to the right. (start = mid + 1)
            //   - If current col is all white, it could be the boundary or boundary is to the left. (end = mid)
            if (row < bottom == checkHasBlackPixel) {
                end = mid;
            } else {
                start = mid + 1;
            }
        }
        return start;
    }

    /**
     * Binary search to find the row boundary.
     * 
     * @param grid          The image matrix.
     * @param start         Start index of row range (inclusive).
     * @param end           End index of row range (exclusive).
     * @param left          Left column index to scan.
     * @param right         Right column index to scan (exclusive).
     * @param checkHasBlackPixel If true, we look for the first row that HAS a black pixel (min boundary).
     *                           If false, we look for the first row that has NO black pixels (max boundary).
     * @return The row index establishing the boundary.
     */
    private static int searchRowBoundary(char[][] grid, int start, int end, int left, int right, boolean checkHasBlackPixel) {
        while (start != end) {
            int mid = (start + end) / 2;
            int col = left;
            // Scan the row 'mid' to see if it contains any black pixel ('1')
            while (col < right && grid[mid][col] == '0') {
                ++col;
            }
            
            // Logic mirrors searchColumnBoundary
            if (col < right == checkHasBlackPixel) {
                end = mid;
            } else {
                start = mid + 1;
            }
        }
        return start;
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
