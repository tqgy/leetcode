package com.carrerup.pratice;

import java.io.IOException;
import java.util.LinkedList;

public class Practice {

	static class TreeNode {
		int value;
		TreeNode left;
		TreeNode right;
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		int start = -6;
		int end = -2;
		for (int i = start; i < end; i += 5) {
			// System.out.println(i);
			System.out.println(i + "~" + (i + 5 > end ? end : i + 5));
		}

		String s = "adsf@dfs@sdf@";
		String[] a = s.split("@");
		System.out.println("length : " + a.length);
		for (int i = 0; i < a.length; i++) {
			System.out.println(a[i]);
		}

	}

	public static void printLevel(TreeNode root) {
		if (root == null) {
			return;
		}
		LinkedList queue = new LinkedList();
		queue.add(root);
		TreeNode end = root;
		int num = 0;
		while (queue.size() != 0) {
			if (root.left != null) {
				queue.add(root.left);
			}
			if (root.right != null) {
				queue.add(root.right);
			}
			num++;
			root = (TreeNode) queue.poll();
			if (root == end) {
				System.out.println(num);
				end = (TreeNode) queue.getLast();
				num = 0;
			}

		}
	}
}
