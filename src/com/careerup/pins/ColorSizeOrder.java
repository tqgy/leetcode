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
 * 1. Identify all unique Sizes (Nodes in our graph).
 * 2. Build a Directed Graph where an edge A -> B means "Size A must come before Size B".
 * 3. Use Topological Sort (DFS-based) to find a linear ordering of the nodes.
 * 4. Detect cycles: We use two sets, 'visiting' (recursion stack) and 'visited' (fully processed),
 *    to detect cycles and avoid re-processing nodes.
 * 
 * The class also extracts the unique Colors in their order of first appearance.
 */
public class ColorSizeOrder {

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
     * Time Complexity: O(V + E), where V is the number of unique sizes and E is the total number of constraints (adjacent pairs in input).
     * Space Complexity: O(V + E) to store the graph and recursion stack.
     * 
     * @param colorSizePairs List of [Color, Size] pairs.
     * @return A list of sizes in a valid topological order.
     * @throws IllegalStateException if a cycle is detected (conflicting constraints).
     */
    private static List<String> resolveSizeOrder(List<String[]> colorSizePairs) {
        // Step 1: Group sizes by color to extract constraints
        // Map: Color -> List of Sizes for that color
        Map<String, List<String>> colorToSizesMap = new LinkedHashMap<>();
        for (String[] pair : colorSizePairs) {
            String color = pair[0];
            String size = pair[1];
            colorToSizesMap.computeIfAbsent(color, k -> new ArrayList<>()).add(size);
        }

        // Step 2: Build the graph
        // Nodes: All unique sizes
        // Edges: Derived from sequential pairs in each color's list
        Set<String> allSizes = new LinkedHashSet<>();
        Map<String, Set<String>> adjacencyList = new HashMap<>();
        
        // Initialize nodes
        for (List<String> sizes : colorToSizesMap.values()) {
            allSizes.addAll(sizes);
        }
        for (String size : allSizes) {
            adjacencyList.put(size, new HashSet<>());
        }

        // Add edges
        for (List<String> sizes : colorToSizesMap.values()) {
            for (int i = 0; i < sizes.size() - 1; i++) {
                String fromSize = sizes.get(i);
                String toSize = sizes.get(i + 1);
                // "fromSize" must come before "toSize"
                adjacencyList.get(fromSize).add(toSize);
            }
        }

        // Step 3: Perform DFS Topological Sort
        List<String> topologicalOrder = new ArrayList<>();
        Set<String> visited = new HashSet<>();  // Nodes fully processed
        Set<String> visiting = new HashSet<>(); // Nodes currently in recursion stack (for cycle detection)

        // Iterate over all nodes to handle disconnected components
        for (String size : allSizes) {
            if (!visited.contains(size)) {
                if (!dfs(size, adjacencyList, visiting, visited, topologicalOrder)) {
                    throw new IllegalStateException("Cycle detected in size constraints. No valid order exists.");
                }
            }
        }

        // DFS pushes to result list after visiting children, so the list is in reverse topological order.
        Collections.reverse(topologicalOrder);
        return topologicalOrder;
    }

    /**
     * Depth First Search helper for Topological Sort.
     * 
     * @param node The current size node being visited.
     * @param adjacencyList The graph adjacency list.
     * @param visiting Set of nodes currently in the recursion stack (ancestors).
     * @param visited Set of nodes that have been fully processed.
     * @param resultList The list to collect nodes in reverse topological order.
     * @return true if successful, false if a cycle is detected.
     */
    private static boolean dfs(String node, Map<String, Set<String>> adjacencyList, 
                               Set<String> visiting, Set<String> visited, List<String> resultList) {
        
        // If we see a node that is currently in the recursion stack, it's a cycle.
        if (visiting.contains(node)) {
            return false;
        }
        // If we already fully processed this node, skip it.
        if (visited.contains(node)) {
            return true;
        }

        // Mark as currently visiting
        visiting.add(node);

        for (String neighbor : adjacencyList.get(node)) {
            if (!dfs(neighbor, adjacencyList, visiting, visited, resultList)) {
                return false;
            }
        }

        // Mark as fully visited and add to result
        visiting.remove(node);
        visited.add(node);
        resultList.add(node);
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
