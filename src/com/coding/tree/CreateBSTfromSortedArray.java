package com.carrerup.tree;

import java.util.LinkedList;  

public class CreateBSTfromSortedArray {  
  
    /** 
     * 题目:给定一个排序数组，如何构造一个二叉排序树 
     * 递归 
     */  
  
    public static void main(String[] args) {  
        int[] data = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };  
        CreateBSTfromSortedArray bst = new CreateBSTfromSortedArray();  
        Node root = bst.createBST(data);  
        bst.inOrder(root);  
        System.out.println();  
        bst.levelOrder(root);  
    }  
  
    public Node createBST(int[] data) {  
        if (data == null || data.length == 0) {  
            return null;  
        }  
        Node[] nodes = createNode(data);  
        return createBSTHelp(nodes, 0, data.length - 1);  
    }  
  
    //Recursion.Node[start...end],find its root.Then find left child and right child for the root.  
    public Node createBSTHelp(Node[] nodes, int start, int end) {  
        if (start > end) {  
            return null;  
        }  
        int mid = start + (end - start) / 2;  
        if (start == end) {  
            return nodes[mid];  
        }  
        Node root = nodes[mid];  
        root.left = createBSTHelp(nodes, start, mid - 1);  
        root.right = createBSTHelp(nodes, mid + 1, end);  
        return root;  
  
    }  
  
    public Node[] createNode(int[] data) {  
        int len = data.length;  
        Node[] nodes = new Node[len];  
        for (int i = 0; i < len; i++) {  
            nodes[i] = new Node(data[i]);  
        }  
        return nodes;  
    }  
  
    private static class Node {  
        int data;  
        Node left;  
        Node right;  
  
        Node(int data) {  
            this.data = data;  
        }  
    }  
  
    public void levelOrder(Node node) {  
        if (node == null)  
            return;  
        LinkedList<Node> queue = new LinkedList<Node>();  
        queue.addLast(node);  
        while (!queue.isEmpty()) {  
            Node curNode = queue.removeFirst();  
            System.out.print(curNode.data + " ");  
            if (curNode.left != null) {  
                queue.addLast(curNode.left);  
            }  
            if (curNode.right != null) {  
                queue.addLast(curNode.right);  
            }  
        }  
    }  
  
    public void inOrder(Node node) {  
        if (node == null) {  
            return;  
        }  
        inOrder(node.left);  
        System.out.print(node.data + " ");  
        inOrder(node.right);  
    }  
  
}  
