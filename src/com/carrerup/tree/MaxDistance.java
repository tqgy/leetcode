package com.carrerup.tree;

public class MaxDistance {

	static class TreeNode{
		int value;
		TreeNode left;
		TreeNode right;
	}
	
	public static int getMaxDistance(TreeNode root){
		if(root == null){
			return 0;
		}else{
			return Math.max(getMaxDistance(root.left), getMaxDistance(root.right))+1;
		}
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
		
		root.left = e1;
		root.right = e2;
		e1.left = e3;
		e1.right = e4;
		e3.left = e5;
		e2.right = e6;
		e6.right = e7;
		e7.right = e8;
		e8.left = e9;
		e9.right = e10;
		
		System.out.println(getMaxDistance(root));
	}

}
