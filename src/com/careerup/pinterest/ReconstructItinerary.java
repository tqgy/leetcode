package com.careerup.pinterest;

import java.util.*;

/**
 * You are given a list of airline tickets where tickets[i] = [from_i, to_i]
 * represent the departure and the arrival airports of one flight. Reconstruct
 * the itinerary in order and return it.
 * 
 * All of the tickets belong to a man who departs from "JFK", thus, the
 * itinerary must begin with "JFK". If there are multiple valid itineraries, you
 * should return the itinerary that has the smallest lexical order when read as
 * a single string.
 * 
 * For example, the itinerary ["JFK", "LGA"] has a smaller lexical order than
 * ["JFK", "LGB"]. You may assume all tickets form at least one valid itinerary.
 * You must use all the tickets once and only once.
 */
public class ReconstructItinerary {
    /**
     * Reconstruct itinerary using all tickets exactly once and returning the
     * lexicographically smallest valid itinerary starting from "JFK".
     *
     * This implementation builds a graph where outgoing edges from each airport
     * are stored in a priority queue (min-heap) so that during DFS we always
     * visit the smallest lexical destination first. We use a Hierholzer-style
     * post-order traversal (remove-edge-on-visit) and then reverse the result
     * to obtain the proper itinerary order.
     *
     * @param tickets list of [from,to] tickets
     * @return itinerary starting at "JFK" that uses every ticket once
     */
    public static List<String> findItinerary(List<List<String>> tickets) {
        Map<String, PriorityQueue<String>> graph = new HashMap<>();
        for (List<String> ticket : tickets) {
            String from = ticket.get(0);
            String to = ticket.get(1);
            graph.putIfAbsent(from, new PriorityQueue<>());
            graph.get(from).offer(to);
        }

        List<String> result = new LinkedList<>();
        dfs("JFK", graph, result);
        return result;
    }

    private static void dfs(String airport, Map<String, PriorityQueue<String>> graph, List<String> result) {
        PriorityQueue<String> pq = graph.get(airport);
        while (pq != null && !pq.isEmpty()) {
            String next = pq.poll();
            dfs(next, graph, result);
        }
        result.addFirst(airport);
    }

    public static void main(String[] args) {
        // Test 1
        List<List<String>> tickets1 = Arrays.asList(
            Arrays.asList("MUC", "LHR"), 
            Arrays.asList("JFK", "MUC"), 
            Arrays.asList("SFO", "SJC"), 
            Arrays.asList("LHR", "SFO"));
        List<String> expected1 = Arrays.asList("JFK", "MUC", "LHR", "SFO", "SJC");
        List<String> out1 = findItinerary(tickets1);
        System.out.println("Test1 expected: " + expected1 + " got: " + out1 + " => " + out1.equals(expected1));

        // Test 2 (lexical tie-breaking)
        List<List<String>> tickets2 = Arrays.asList(
            Arrays.asList("JFK","SFO"), 
            Arrays.asList("JFK","ATL"), 
            Arrays.asList("SFO","ATL"), 
            Arrays.asList("ATL","JFK"), 
            Arrays.asList("ATL","SFO"));
        List<String> expected2 = Arrays.asList("JFK","ATL","JFK","SFO","ATL","SFO");
        List<String> out2 = findItinerary(tickets2);
        System.out.println("Test2 expected: " + expected2 + " got: " + out2 + " => " + out2.equals(expected2));

        // Test 3 (from problem examples with multiple options)
        List<List<String>> tickets3 = Arrays.asList(
            Arrays.asList("JFK","KUL"),
            Arrays.asList("JFK","NRT"), 
            Arrays.asList("NRT","JFK"));
        List<String> expected3 = Arrays.asList("JFK","NRT","JFK","KUL");
        List<String> out3 = findItinerary(tickets3);
        System.out.println("Test3 expected: " + expected3 + " got: " + out3 + " => " + out3.equals(expected3));

        // Test 4: tickets are disconnected from JFK (expected invalid itinerary)
        List<List<String>> tickets4 = Arrays.asList(
            Arrays.asList("SFO","NRT"),
            Arrays.asList("SFO","NRT"),
            Arrays.asList("NRT","SFO"));
        List<String> out4 = findItinerary(tickets4);
        List<String> expected4 = Arrays.asList("JFK"); // only starting point
        System.out.println("Test4 (disconnected) itinerary: " + out4 + " => " + out4.equals(expected4));

        // Test 5: completely unrelated tickets (no JFK)
        List<List<String>> tickets5 = Arrays.asList(
            Arrays.asList("A","B"),
            Arrays.asList("C","D")
        );
        List<String> out5 = findItinerary(tickets5);
        List<String> expected5 = Arrays.asList("JFK"); 
        System.out.println("Test5 (no JFK) itinerary: " + out5 + " => " + out5.equals(expected5));
    }

}
