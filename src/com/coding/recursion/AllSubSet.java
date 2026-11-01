package com.carrerup.recursion;

import java.util.ArrayList;

public class AllSubSet {

	/**
	 * Write a method that returns all subsets of a set. We should first have
	 * some reasonable expectations of our time and space complexity. How many
	 * subsets of a set are there? We can compute this by realizing that when we
	 * generate a subset, each element has the “choice�?of either being in there
	 * or not. That is, for the first element, there are 2 choices. For the
	 * second, there are two, etc. So, doing 2 * 2 * ... * 2 n times gives us
	 * 2^n subsets. We will not be able to do better than this in time or space
	 * complexity.
	 */

	/*
	 * Approach #1: Recursion This is a great problem to implement with
	 * recursion since we can build all subsets of a set using all subsets of a
	 * smaller set. Specifically, given a set S, we can do the following
	 * recursively: »»Let first = S[0]. Let smallerSet = S[1, ... , n].
	 * »»Compute all subsets of smallerSet and put them in allsubsets. »»For
	 * each subset in allsubsets, clone it and add first to the subset.
	 */
	ArrayList<ArrayList<Integer>> getSubsets(ArrayList<Integer> set, int index) {
		ArrayList<ArrayList<Integer>> allsubsets;
		if (set.size() == index) {
			allsubsets = new ArrayList<ArrayList<Integer>>();
			allsubsets.add(new ArrayList<Integer>()); // Empty set
		} else {
			allsubsets = getSubsets(set, index + 1);
			int item = set.get(index);
			ArrayList<ArrayList<Integer>> moresubsets = new ArrayList<ArrayList<Integer>>();
			for (ArrayList<Integer> subset : allsubsets) {
				ArrayList<Integer> newsubset = new ArrayList<Integer>();
				newsubset.addAll(subset); //
				newsubset.add(item);
				moresubsets.add(newsubset);
			}
			allsubsets.addAll(moresubsets);
		}
		return allsubsets;
	}

	/*
	 * Approach #2: Combinatorics »»When we’re generating a set, we have two
	 * choices for each element: (1) the element is
	 * 
	 * in the set (the “yes�?state) or (2) the element is not in the set (the
	 * “no�?state). This means that each subset is a sequence of yesses /
	 * nos—e.g., “yes, yes, no, no, yes, no�?»»This gives us 2^n possible
	 * subsets. How can we iterate through all possible sequences of “yes�?/
	 * “no�?states for all elements? If each “yes�?can be treated as a 1 and
	 * each “no�?can be treated as a 0, then each subset can be represented as a
	 * binary string. »»Generating all subsets then really just comes down to
	 * generating all binary numbers (that is, all integers). Easy!
	 */
	ArrayList<ArrayList<Integer>> getSubsets2(ArrayList<Integer> set) {
		ArrayList<ArrayList<Integer>> allsubsets = new ArrayList<ArrayList<Integer>>();
		int max = 1 << set.size();
		for (int i = 0; i < max; i++) {
			ArrayList<Integer> subset = new ArrayList<Integer>();
			int k = i;
			int index = 0;
			while (k > 0) {
				if ((k & 1) > 0) {
					subset.add(set.get(index));
				}
				k >>= 1;
				index++;
			}
			allsubsets.add(subset);
		}
		return allsubsets;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
