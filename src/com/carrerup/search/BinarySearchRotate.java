package com.carrerup.search;

public class BinarySearchRotate {

	/**
	 * Suppose a sorted array is rotated at some pivot unknown to you
	 * beforehand. (i.e., 0 1 2 4 5 6 7 might become 4 5 6 7 0 1 2). You are
	 * given a target value to search. If found in the array return its index,
	 * otherwise return -1. You may assume no duplicate exists in the array.
	 */
	private static int search(int A[], int n, int target) {
		int left = 0, right = n - 1;
		while (left <= right) {
			// int mid = (left + right) / 2;
			int mid = left + (right - left) / 2;
			if (A[mid] == target)
				return mid;
			if (A[mid] < A[right]) {
				if (target > A[mid] && target <= A[right])
					left = mid + 1;
				else
					right = mid - 1;
			} else if (A[mid] > A[right]) {
				if (target < A[mid] && target >= A[left])
					right = mid - 1;
				else
					left = mid + 1;
			} else
				--right;
		}
		return -1;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] a = { 4, 5, 6, 7, 0, 1, 2, 3 };
		System.out.println(search(a, a.length, 2));
	}

}
