package com.carrerup.recursion;

import java.awt.Point;
import java.util.ArrayList;

public class GraphPath {

	/**
	 * Imagine a robot sitting on the upper left hand corner of an NxN grid. The
	 * robot can only move in two directions: right and down. How many possible
	 * paths are there for the robot?
	 * 
	 */
	/*
	 * SOLUTION Part 1: (For clarity, we will solve this part assuming an X by Y
	 * grid) Each path has (X-1)+(Y-1) steps. Imagine the following paths: X X Y
	 * Y X (move right -> right -> down -> down -> right) X Y X Y X (move right
	 * -> down -> right -> down -> right) ... Each path can be fully represented
	 * by the moves at which we move right. That is, if I were to ask you which
	 * path you took, you could simply say â€œI moved right on step 3 and 4.â€?
	 * Since you must always move right X-1 times, and you have X-1 + Y-1 total
	 * steps, you have to pick X-1 times to move right out of X-1+Y-1 choices.
	 * Thus, there are C(X-1, X-1+Y-1) paths (e.g., X-1+Y-1 choose X-1): (X-1 +
	 * Y-1)! / ((X-1)! * (Y-1)!)
	 */
	static ArrayList<Point> current_path = new ArrayList<Point>();

	public static boolean getPaths(int x, int y) {
		Point p = new Point(x, y);
		current_path.add(p);
		if (0 == x && 0 == y)
			return true; // current_path
		boolean success = false;
		if (x >= 1 && isFree(x - 1, y)) { // Try right
			success = getPaths(x - 1, y); // Free! Go right
		}
		if (!success && y >= 1 && isFree(x, y - 1)) { // Try down
			success = getPaths(x, y - 1); // Free! Go down
		}
		if (!success) {
			current_path.remove(p); // Wrong way!
		}
		return success;
	}

	public static boolean isFree(int x, int y) {
		return true;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
