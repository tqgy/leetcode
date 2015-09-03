package com.carrerup.list;

public class ReverseLinkedList {

	static class NodeList {
		int value;
		NodeList next;

		public NodeList(int value) {
			this.value = value;
		}
	}

	public static void printNodeList(NodeList head) {
		while (head != null) {
			System.out.print(" " + head.value + " ");
			head = head.next;
		}
	}

	public static NodeList reverseNodeList(NodeList root) {
		if (root == null) {
			return null;
		}
		NodeList pre = null;
		while (root != null) {
			NodeList next = root.next;
			root.next = pre;
			pre = root;
			root = next;
		}
		return pre;
	}

	/**
	 * Implement an algorithm to find the nth to last element of a singly linked
	 * list. 
	 * SOLUTION 
	 * Note: This problem screams recursion, but we’ll do it a different way because it’s trickier. 
	 * In a question like this, expect follow up questions about the advantages of recursion vs iteration.
	 * 
	 * Assumption: The minimum number of nodes in list is n. 
	 * 
	 * Algorithm: 
	 * 1. Create two pointers, p1 and p2, that point to the beginning of the node.
	 * 2. Increment p2 by n-1 positions, to make it point to the nth node from the beginning 
	 * 		(to make the distance of n between p1 and p2). 
	 * 3. Check for p2->next == null if yes return value of p1, otherwise increment p1 and p2. 
	 * 		If next of p2 is null it means p1 points to the nth node from the last as the distance between the two is n. 
	 * 4. Repeat Step 3.
	 * 
	 * @param head
	 * @param n
	 * @return
	 */
	NodeList nthToLast(NodeList head, int n) {
		if (head == null || n < 1) {
			return null;
		}
		NodeList p1 = head;
		NodeList p2 = head;
		for (int j = 0; j < n - 1; ++j) { // skip n-1 steps ahead
			if (p2 == null) {
				return null; // not found since list size < n
			}
			p2 = p2.next;
		}
		while (p2.next != null) {
			p1 = p1.next;
			p2 = p2.next;
		}
		return p1;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		NodeList head1 = new NodeList(1);
		NodeList head2 = new NodeList(2);
		NodeList head3 = new NodeList(3);
		NodeList head4 = new NodeList(4);
		NodeList head5 = new NodeList(5);

		head1.next = head2;
		head2.next = head3;
		head3.next = head4;
		head4.next = head5;

		printNodeList(head1);
		printNodeList(reverseNodeList(head1));

	}

}
