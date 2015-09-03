package com.carrerup.dp;

public class MaximumSubArray {

	/**
	 * Find the contiguous subarray within an array (containing at least one
	 * number) which has the largest sum. For example, given the array
	 * [âˆ?,1,âˆ?,4,âˆ?,2,1,âˆ?,4], the contiguous subarray [4,âˆ?,2,1] has the
	 * largest sum = 6.
	 */
	static int maxSubArray(int A[], int n) {
		int ret = A[0], sum = 0;
		for (int i = 0; i < n; ++i) {
			sum = sum + A[i] > A[i] ? sum + A[i] : A[i];
			ret = ret > sum ? ret : sum;
		}
		return ret;
	}

	/**
	 * 6. Best time to buy and sell stock Say you have an array for which the
	 * ith element is the price of a given stock on day i. If you were only
	 * permitted to complete at most one transaction (ie, buy one and sell one
	 * share of the stock), design an algorithm to find the maximum profit.
	 */
	static int maxProfit(int[] prices) {
		int maxVal = 0;
		int minVal = Integer.MAX_VALUE;
		for (int i = 0; i < prices.length; i++) {
			if (prices[i] < minVal)
				minVal = prices[i];
			maxVal = maxVal > prices[i] - minVal ? maxVal : prices[i] - minVal;
		}
		return maxVal;
	}

	/**
	 * Find the max and min elements of an unsorted integer array using a
	 * minimal number of comparisons
	 */
	static void max_min(int a[], int n, int max, int min) {
		if (n <= 0)
			return;
		if (n == 1) {
			max = min = a[0];
			return;
		}

		for (int i = 0; i < n; i += 2)
			if (i < n && a[i] < a[i + 1]) {
				int tmp = a[i];
				a[i] = a[i + 1];
				a[i + 1] = tmp;
			}

		max = a[0];
		for (int i = 2; i < n; i += 2)
			if (max < a[i])
				max = a[i];

		min = a[1];
		for (int i = 1; i < n; i += 2)
			if (min > a[i])
				min = a[i];
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] prices = { 5, 6, 7, 8, 9, 1, 2, 3, 4 };
		int profit = maxProfit(prices);
		System.out.println(profit);
	}

}
