package com.careerup.strip;

import java.util.*;

/**
 * Clean and simplified version of the Python shipping‑route logic.
 * Supports:
 *   1. travelCost: direct cost for (source → target, method)
 *   2. travelRoute: all routes with ≤1 transfer
 *   3. travelCheapest: cheapest route with ≤1 transfer
 *   4. travelCheapestAnyTransfer: cheapest route with unlimited transfers (Dijkstra)
 */
public class ShippingCost1 {

    /** Represents one directed shipping edge */
    static class Route {
        String target;
        String method;
        int cost;

        Route(String target, String method, int cost) {
            this.target = target;
            this.method = method;
            this.cost = cost;
        }
    }

    /** Parsed graph + cost lookup */
    static class ParsedInput {
        Map<String, List<Route>> graph = new HashMap<>();
        Map<String, Integer> costLookup = new HashMap<>();
    }

    // ------------------------------------------------------------
    // Parsing
    // ------------------------------------------------------------
    static ParsedInput parseInput(String input) {
        ParsedInput parsed = new ParsedInput();

        for (String part : input.split(",")) {
            String[] p = part.split(":");
            String src = p[0], dst = p[1], method = p[2];
            int cost = Integer.parseInt(p[3]);

            parsed.graph.computeIfAbsent(src, k -> new ArrayList<>())
                        .add(new Route(dst, method, cost));

            parsed.costLookup.put(src + "|" + dst, cost);
        }
        return parsed;
    }

    // ------------------------------------------------------------
    // Q1: Direct cost for (source, target, method)
    // ------------------------------------------------------------
    static Object travelCost(String input, String src, String dst, String method) {
        ParsedInput parsed = parseInput(input);

        if (!parsed.graph.containsKey(src))
            return "source_country not in ship_graph";

        List<Integer> results = new ArrayList<>();
        for (Route r : parsed.graph.get(src)) {
            if (r.target.equals(dst) && r.method.equals(method)) {
                results.add(r.cost);
            }
        }

        return results.isEmpty()
                ? "target_country or method not in ship_graph[src]"
                : results;
    }

    // ------------------------------------------------------------
    // Q2: All routes with ≤1 transfer
    // ------------------------------------------------------------
    static Object travelRoute(String input, String src, String dst) {
        ParsedInput parsed = parseInput(input);

        if (!parsed.graph.containsKey(src))
            return "source_country not in ship_graph";

        List<Map<String, Object>> results = new ArrayList<>();

        for (Route r1 : parsed.graph.get(src)) {
            String mid = r1.target;

            // Direct
            if (mid.equals(dst)) {
                results.add(routeEntry(List.of(src, dst),
                                       List.of(r1.method),
                                       r1.cost));
            }

            // One transfer
            if (parsed.graph.containsKey(mid)) {
                for (Route r2 : parsed.graph.get(mid)) {
                    if (r2.target.equals(dst)) {
                        results.add(routeEntry(List.of(src, mid, dst),
                                               List.of(r1.method, r2.method),
                                               r1.cost + r2.cost));
                    }
                }
            }
        }

        return results.isEmpty() ? "route not in ship_graph" : results;
    }

    private static Map<String, Object> routeEntry(List<String> path, List<String> methods, int cost) {
        Map<String, Object> map = new HashMap<>();
        map.put("route", path);
        map.put("method", methods);
        map.put("cost", cost);
        return map;
    }

    // ------------------------------------------------------------
    // Q3: Cheapest route with ≤1 transfer
    // ------------------------------------------------------------
    static Object travelCheapest(String input, String src, String dst) {
        ParsedInput parsed = parseInput(input);

        if (!parsed.graph.containsKey(src))
            return "source_country not in ship_graph";

        String key = src + "|" + dst;
        parsed.costLookup.putIfAbsent(key, Integer.MAX_VALUE);

        for (Route r1 : parsed.graph.get(src)) {
            String mid = r1.target;

            // Direct
            if (mid.equals(dst)) {
                parsed.costLookup.put(key, Math.min(parsed.costLookup.get(key), r1.cost));
            }

            // One transfer
            if (parsed.graph.containsKey(mid)) {
                for (Route r2 : parsed.graph.get(mid)) {
                    if (r2.target.equals(dst)) {
                        int total = r1.cost + r2.cost;
                        parsed.costLookup.put(key, Math.min(parsed.costLookup.get(key), total));
                    }
                }
            }
        }

        return parsed.costLookup.get(key) == Integer.MAX_VALUE
                ? "(source, target) not in cost_lookup"
                : parsed.costLookup.get(key);
    }

    // ------------------------------------------------------------
    // Q4: Cheapest route with unlimited transfers (Dijkstra)
    // ------------------------------------------------------------
    static Object travelCheapestAnyTransfer(String input, String src, String dst) {
        ParsedInput parsed = parseInput(input);

        if (!parsed.graph.containsKey(src))
            return "source_country not in ship_graph";

        record State(int cost, String node, List<String> path, List<String> methods) {}

        PriorityQueue<State> pq = new PriorityQueue<>(Comparator.comparingInt(s -> s.cost));
        pq.add(new State(0, src, List.of(src), List.of()));

        Set<String> visited = new HashSet<>();

        while (!pq.isEmpty()) {
            State cur = pq.poll();

            if (cur.node.equals(dst)) {
                return Map.of(
                        "path", cur.path,
                        "method", cur.methods,
                        "cost", cur.cost
                );
            }

            String visitKey = cur.node + "|" + cur.path.size();
            if (!visited.add(visitKey)) continue;

            for (Route r : parsed.graph.getOrDefault(cur.node, List.of())) {
                if (cur.path.contains(r.target)) continue; // avoid cycles

                List<String> newPath = new ArrayList<>(cur.path);
                newPath.add(r.target);

                List<String> newMethods = new ArrayList<>(cur.methods);
                newMethods.add(r.method);

                pq.add(new State(cur.cost + r.cost, r.target, newPath, newMethods));
            }
        }

        return "(source, target) not reachable";
    }

    // ------------------------------------------------------------
    // Demo
    // ------------------------------------------------------------
    public static void main(String[] args) {
        String input = "US:UK:FEDEX:5,US:CA:UPS:1,CA:FR:DHL:3,UK:FR:DHL:2,US:FR:UPS:1";

        System.out.println("Q1:");
        System.out.println(travelCost(input, "US", "UK", "FEDEX"));
        System.out.println(travelCost(input, "CN", "UK", "FEDEX"));
        System.out.println(travelCost(input, "US", "CN", "FEDEX"));
        System.out.println(travelCost(input, "US", "UK", "CN"));

        System.out.println("\nQ2:");
        System.out.println(travelRoute(input, "US", "FR"));

        System.out.println("\nQ3:");
        System.out.println(travelCheapest(input, "US", "FR"));

        System.out.println("\nQ4:");
        System.out.println(travelCheapestAnyTransfer(input, "US", "FR"));
    }
}

