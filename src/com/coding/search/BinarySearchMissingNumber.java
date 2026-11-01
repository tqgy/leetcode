package com.carrerup.search;

public class BinarySearchMissingNumber {

	/**
	 * You have an array a[] and the length n, the array should filled from 0 to
	 * n-1 but now one number missed. Find the missing number. For example, to
	 * the array {0,1,3,4,5,6,7}, the missing number is 2.
	 */
	static int findMissing(int a[], int n) {
		int left = 0;
		int right = n - 1;
		while (left <= right) {
			int mid = (left + right) / 2;

			if (mid == 0 && a[mid] != 0)
				return 0;

			if (mid != 0 && a[mid - 1] + 1 != a[mid])
				return a[mid] - 1;

			if (mid != n && a[mid + 1] - 1 != a[mid])
				return a[mid] + 1;

			if (a[mid] == mid)
				left = mid + 1;
			else
				right = mid - 1;
		}
		return -1;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] a = { 0, 1, 3, 4, 5, 6, 7 };
		int s = findMissing(a, 7);
		System.out.println(s);
	}

}
