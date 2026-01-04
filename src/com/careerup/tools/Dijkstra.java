package com.careerup.tools;

import java.util.*;

public class Dijkstra {

    static void dijkstra(ArrayList<ArrayList<int[]>> graph, int src) {
        int nodeCount = graph.size();

        // minDistances[i] stores the shortest distance from sourceVertex to i
        int[] dist = new int[nodeCount];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[src] = 0;

        // predecessors[i] stores the previous node in the shortest path to i
        int[] pre = new int[nodeCount];
        Arrays.fill(pre, -1);

        // Min-heap storing {distance, node}, ordered by distance
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);
        pq.offer(new int[] { 0, src });

        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int curDist = cur[0];
            int curNode = cur[1];

            // If we found a shorter path to currentNode before processing this, skip
            if (curDist > dist[curNode])
                continue;

            // Visit neighbors
            for (int[] edge : graph.get(curNode)) {
                int neibNode = edge[0];
                int weight = edge[1];

                // Relaxation step: found a shorter path to neighborNode?
                if (dist[curNode] + weight < dist[neibNode]) {
                    dist[neibNode] = dist[curNode] + weight;
                    pre[neibNode] = curNode; // Track the path
                    pq.offer(new int[] { dist[neibNode], neibNode });
                }
            }
        }

        // Print results
        printSolution(dist, pre);
    }

    static void addEdge(ArrayList<ArrayList<int[]>> graph, int u, int v, int weight) {
        graph.get(u).add(new int[] { v, weight });
        graph.get(v).add(new int[] { u, weight }); // Undirected graph
    }

    static void printSolution(int[] minDistances, int[] predecessors) {
        System.out.println("Vertex\t Distance\tPath");
        for (int i = 0; i < minDistances.length; i++) {
            System.out.print(i + " \t\t " + minDistances[i] + "\t\t\t");
            printPath(i, predecessors);
            System.out.println();
        }
    }

    // Recursively print path from source to current node v
    static void printPath(int currentVertex, int[] predecessors) {
        if (currentVertex == -1)
            return;
        printPath(predecessors[currentVertex], predecessors);
        System.out.print(currentVertex + " ");
    }

    public static void main(String[] args) {
        int vertexCount = 5; // Number of vertices
        int sourceVertex = 0; // Source vertex

        // Adjacency list to represent the graph: index -> list of {neighbor, weight}
        ArrayList<ArrayList<int[]>> graph = new ArrayList<>();
        for (int i = 0; i < vertexCount; i++) {
            graph.add(new ArrayList<>());
        }

        // Add edges: u -> v with weight w
        addEdge(graph, 0, 1, 4);
        addEdge(graph, 0, 2, 8);
        addEdge(graph, 1, 4, 6);
        addEdge(graph, 1, 2, 3);
        addEdge(graph, 2, 3, 2);
        addEdge(graph, 3, 4, 10);

        // Run Dijkstra's algorithm
        dijkstra(graph, sourceVertex);
    }
}
