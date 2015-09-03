package com.carrerup.tree;

public class NearestFatherNode {

	static class TreeNode {
		int data;
		TreeNode left;
		TreeNode right;

	}

	/**
	 * That is, if p and q are both on the left of the node, branch left to look
	 * for the common ancestor. When p and q are no longer on the same side, you
	 * must have found the first common ancestor.
	 * 
	 * @param root
	 * @param one
	 * @param two
	 * @return
	 */
	public static TreeNode getFather(TreeNode root, TreeNode one, TreeNode two) {
		if (cover(root.left, one) && cover(root.left, two)) {
			return getFather(root.left, one, two);
		}
		if (cover(root.right, one) && cover(root.right, two)) {
			return getFather(root.right, one, two);
		}
		return root;
	}

	private static boolean cover(TreeNode root, TreeNode p) {
		if (root == null)
			return false;
		if (root == p)
			return true;
		return (cover(root.left, p) || cover(root.right, p));

	}

	/**
	 * You have two very large binary trees: T1, with millions of nodes, and T2,
	 * with hundreds of nodes. Create an algorithm to decide if T2 is a subtree
	 * of T1. pg 54 SOLUTION Note that the problem here specifies that T1 has
	 * millions of nodes—this means that we should be careful of how much space
	 * we use. Let’s say, for example, T1 has 10 million nodes—this means that
	 * the data alone is about 40 mb. We could create a string representing the
	 * inorder and preorder traversals. If T2’s preorder traversal is a
	 * substring of T1’s preorder traversal, and T2’s inorder traversal is a
	 * substring of T1’s inorder traversal, then T2 is a substring of T1. We can
	 * check this using a suffix tree. However, we may hit memory limitations
	 * because suffix trees are extremely memory intensive. If this become an
	 * issue, we can use an alternative approach. Alternative Approach: The
	 * treeMatch procedure visits each node in the small tree at most once and
	 * is called no more than once per node of the large tree. Worst case
	 * runtime is at most O(n * m), where n and m are the sizes of trees T1 and
	 * T2, respectively. If k is the number of occurrences of T2’s root in T1,
	 * the worst case runtime can be characterized as O(n + k * m).
	 * 
	 * @param t1
	 * @param t2
	 * @return
	 */
	boolean containsTree(TreeNode t1, TreeNode t2) {
		if (t2 == null)
			return true; // The empty tree is always a subtree
		else
			return subTree(t1, t2);
	}

	boolean subTree(TreeNode r1, TreeNode r2) {
		if (r1 == null)
			return false; // big tree empty & subtree still not found.
		if (r1.data == r2.data) {
			if (matchTree(r1, r2))
				return true;
		}
		return (subTree(r1.left, r2) || subTree(r1.right, r2));
	}

	boolean matchTree(TreeNode r1, TreeNode r2) {
		if (r2 == null && r1 == null)
			return true; // nothing left in the subtree
		if (r1 == null || r2 == null)
			return false; // big tree empty & subtree still not found
		if (r1.data != r2.data)
			return false; // data doesn’t match
		return (matchTree(r1.left, r2.left) && matchTree(r1.right, r2.right));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TreeNode root = new TreeNode();
		TreeNode e1 = new TreeNode();
		TreeNode e2 = new TreeNode();
		TreeNode e3 = new TreeNode();
		TreeNode e4 = new TreeNode();
		TreeNode e5 = new TreeNode();
		TreeNode e6 = new TreeNode();
		TreeNode e7 = new TreeNode();
		TreeNode e8 = new TreeNode();
		TreeNode e9 = new TreeNode();
		TreeNode e10 = new TreeNode();
		root.data = 0;
		e1.data = 1;
		e2.data = 2;
		e3.data = 3;
		e4.data = 4;
		e5.data = 5;
		e6.data = 6;
		e7.data = 7;
		e8.data = 8;
		e9.data = 9;
		e10.data = 10;

		root.left = e1;
		root.right = e2;
		e1.left = e3;
		e1.right = e4;
		e3.left = e5;
		e2.right = e6;
		e6.left = e7;
		e6.right = e8;
		e8.left = e9;
		e8.right = e10;

		TreeNode r = getFather(root, e7, e10);
		System.out.println(r.data);
	}

}
