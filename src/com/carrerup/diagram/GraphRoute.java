package com.carrerup.diagram;

import java.util.ArrayList;
import java.util.LinkedList;

public class GraphRoute {

	/**
	 * Given a directed graph, design an algorithm to find out whether there is
	 * a route between two nodes. SOLUTION This problem can be solved by just
	 * simple graph traversal, such as depth first search or breadth first
	 * search. We start with one of the two nodes and, during traversal, check
	 * if the other node is found. We should mark any node found in the course
	 * of the algorithm as â€˜already visitedâ€?to avoid cycles and repetition of
	 * the nodes.
	 * 
	 * @author guyu
	 * 
	 */
	public enum State {
		Unvisited, Visited, Visiting;
	}

	private static class Graph {
		ArrayList<Node> nodes;

		ArrayList<Node> getNodes() {
			return nodes;
		}
	}

	private static class Node {
		State state;
		ArrayList<Node> adjacent;

		ArrayList<Node> getAdjacent() {
			return adjacent;
		}
	}

	public static boolean search(Graph g, Node start, Node end) {
		LinkedList<Node> q = new LinkedList<Node>(); // operates as Stack
		for (Node u : g.getNodes()) {
			u.state = State.Unvisited;
		}
		start.state = State.Visiting;
		q.add(start);
		Node u;
		while (!q.isEmpty()) {
			u = q.removeFirst(); // i.e., pop()
			if (u != null) {
				for (Node v : u.getAdjacent()) {
					if (v.state == State.Unvisited) {
						if (v == end) {
							return true;
						} else {
							v.state = State.Visiting;
							q.add(v);
						}
					}
				}
				u.state = State.Visited;
			}
		}
		return false;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
