package com.careerup.pins;

import java.util.*;

/**
 * Solves the "Color Size Order" problem using Topological Sort.
 * 
 * Problem:
 * We are given a list of items, each having a Color and a Size (e.g., ["Red", "S"]).
 * The items are provided in a specific sequence. For each unique Color, the sequence
 * of Sizes it appears with defines a mandatory relative ordering.
 * 
 * Example Input:
 * - Red: S, M, L  => Constraints: S -> M, M -> L
 * - Blue: XS, S, L => Constraints: XS -> S, S -> L
 * 
 * Goal:
 * Determine a single global list of Sizes (e.g., [XS, S, M, L]) that satisfies ALL 
 * ordering constraints derived from every Color.
 * 
 * Approach:
 * 1. Identify all unique Sizes and map each to an integer ID [0, N-1].
 * 2. Build a Directed Graph using an adjacency list (List of Lists of Integers).
 * 3. Use Topological Sort (DFS-based) with an int[] array for state tracking.
 * 4. Detect cycles: If a cycle exists, no valid order exists.
 * 
 * This implementation uses primitive arrays for state management to avoid Map overhead during traversal.
 */
public class ColorSizeOrder {

    // Constants for DFS State
    private static final int UNVISITED = 0;
    private static final int VISITING = 1; // Currently in recursion stack
    private static final int VISITED = 2;  // Fully processed

    /**
     * Extracts unique colors from the input list, preserving the order of their first appearance.
     * 
     * @param colorSizePairs List of [Color, Size] arrays.
     * @return List of unique color strings.
     */
    private static List<String> extractColors(List<String[]> colorSizePairs) {
        List<String> result = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (String[] pair : colorSizePairs) {
            String color = pair[0];
            if (seen.add(color)) {
                result.add(color);
            }
        }
        return result;
    }

    /**
     * Builds a global ordering of sizes that respects all per-color constraints using Topological Sort.
     * 
     * Time Complexity: O(V + E), where V is the number of unique sizes and E is the total number of constraints.
     * Space Complexity: O(V + E) to store the graph and recursion stack.
     * 
     * @param colorSizePairs List of [Color, Size] pairs.
     * @return A list of sizes in a valid topological order.
     * @throws IllegalStateException if a cycle is detected (conflicting constraints).
     */
    private static List<String> resolveSizeOrder(List<String[]> colorSizePairs) {
        // Step 1: Collect unique sizes and map them to integers
        Set<String> uniqueSizes = new LinkedHashSet<>();
        // Group input by color to easily extract sequential pairs later
        Map<String, List<String>> colorToSizesMap = new LinkedHashMap<>();

        for (String[] pair : colorSizePairs) {
            String color = pair[0];
            String size = pair[1];
            uniqueSizes.add(size);
            colorToSizesMap.computeIfAbsent(color, k -> new ArrayList<>()).add(size);
        }

        int numSizes = uniqueSizes.size();
        Map<String, Integer> sizeToIndex = new HashMap<>();
        String[] indexToSize = new String[numSizes];
        
        int index = 0;
        for (String size : uniqueSizes) {
            sizeToIndex.put(size, index);
            indexToSize[index] = size;
            index++;
        }

        // Step 2: Build the graph using integer IDs
        List<List<Integer>> adj = new ArrayList<>(numSizes);
        for (int i = 0; i < numSizes; i++) {
            adj.add(new ArrayList<>());
        }

        for (List<String> sizes : colorToSizesMap.values()) {
            for (int i = 0; i < sizes.size() - 1; i++) {
                int fromIdx = sizeToIndex.get(sizes.get(i));
                int toIdx = sizeToIndex.get(sizes.get(i + 1));
                adj.get(fromIdx).add(toIdx);
            }
        }

        // Step 3: Perform DFS Topological Sort
        // visited array stores state: 0=UNVISITED, 1=VISITING, 2=VISITED
        int[] visited = new int[numSizes];
        List<String> topologicalOrder = new ArrayList<>();

        for (int i = 0; i < numSizes; i++) {
            if (visited[i] == UNVISITED) {
                if (!dfs(i, adj, visited, topologicalOrder, indexToSize)) {
                    throw new IllegalStateException("Cycle detected in size constraints. No valid order exists.");
                }
            }
        }

        // DFS pushes to result list after visiting children, so the list is in reverse topological order.
        Collections.reverse(topologicalOrder);
        return topologicalOrder;
    }

    /**
     * Depth First Search helper using integer indices and array-based state.
     * 
     * @param u Current node index.
     * @param adj Adjacency list.
     * @param visited Array tracking visitor state.
     * @param resultList List to collect result strings.
     * @param indexToSize Mapping from index back to size String.
     * @return true if successful, false if a cycle is detected.
     */
    private static boolean dfs(int u, List<List<Integer>> adj, int[] visited, 
                               List<String> resultList, String[] indexToSize) {
        
        visited[u] = VISITING;

        for (int v : adj.get(u)) {
            if (visited[v] == VISITING) {
                // Cycle detected
                return false;
            }
            if (visited[v] == UNVISITED) {
                if (!dfs(v, adj, visited, resultList, indexToSize)) {
                    return false;
                }
            }
        }

        visited[u] = VISITED;
        resultList.add(indexToSize[u]);
        return true;
    }

    public static void main(String[] args) {
        List<String[]> items = Arrays.asList(
            new String[] { "Red", "S" }, 
            new String[] { "Red", "M" },
            new String[] { "Red", "L" }, 
            new String[] { "Blue", "XS" }, 
            new String[] { "Blue", "S" },
            new String[] { "Blue", "L" }
        );

        try {
            List<String> colors = extractColors(items);
            List<String> sizes = resolveSizeOrder(items);

            System.out.println("Colors: " + colors);
            System.out.println("Sizes: " + sizes);
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
        }
    }
}
