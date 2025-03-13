package com.carrerup.tree;

import java.util.ArrayList;
import java.util.LinkedList;

import com.carrerup.tree.MaxDistance.TreeNode;

public class LevelVisitTree {

	/**
	 * Given a binary search tree, design an algorithm which creates a linked
	 * list of all the nodes at each depth (eg, if you have a tree with depth D,
	 * you’ll have D linked lists). SOLUTION: We can do a simple level by level
	 * traversal of the tree, with a slight modification of the breath-first
	 * traversal of the tree. In a usual breath first search traversal, we
	 * simply traverse the nodes without caring which level we are on. In this
	 * case, it is critical to know the level. We thus use a dummy node to
	 * indicate when we have finished one level and are starting on the next.
	 */
	ArrayList<LinkedList<TreeNode>> findLevelLinkList(TreeNode root) {
		int level = 0;
		ArrayList<LinkedList<TreeNode>> result = new ArrayList<LinkedList<TreeNode>>();
		LinkedList<TreeNode> list = new LinkedList<TreeNode>();
		list.add(root);
		result.add(level, list);
		while (true) {
			list = new LinkedList<TreeNode>();
			for (int i = 0; i < result.get(level).size(); i++) {
				TreeNode n = (TreeNode) result.get(level).get(i);
				if (n != null) {
					if (n.left != null)
						list.add(n.left);
					if (n.right != null)
						list.add(n.right);
				}
			}
			if (list.size() > 0) {
				result.add(level + 1, list);
			} else {
				break;
			}
			level++;
		}
		return result;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
