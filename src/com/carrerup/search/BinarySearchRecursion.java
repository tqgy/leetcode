package com.carrerup.search;

/**
 * 二分查找，�?算法导论》，习题2.3-5 Referring back to the searching problem (see Exercise
 * 2.1-3), observe that if the sequence A is sorted, we can check the midpoint
 * of the sequence against v and eliminate half of the sequence from further
 * consideration. Binary search is an algorithm that repeats this procedure,
 * halving the size of the remaining portion of the sequence each time. Write
 * pseudocode, either iterative or recursive, for binary search. Argue that the
 * worst-case running time of binary search is Θ(lg n). 附：习题2.1-3 Consider the
 * searching problem: �?Input: A sequence of n numbers A = a1, a2, . . . , an
 * and a value v. �?Output: An index i such that v = A[i] or the special value
 * NIL if v does not appear in A. Write pseudocode for linear search, which
 * scans through the sequence, looking for v. Using a loop invariant, prove that
 * your algorithm is correct. Make sure that your loop invariant fulfills the
 * three necessary properties.
 * 本文地址：http://mushiqianmeng.blog.51cto.com/3970029/731203
 * 
 * @author lihzh(苦�?coder)
 */
public class BinarySearchRecursion {

	public static void main(String[] args) {
		// 根据题意，给定一个已经排好序的数�?
		int[] input = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13 };
		// 给定查找目标
		int target = 6;
		// 初始�?到数组最后以后一位为范围
		String result = binarySearch(input, target, 0, input.length - 1);
		// 打印输出
		System.out.println(result);
	}

	/**
	 * 二分查找
	 * 
	 * @param input
	 *            给定已排序的待查数组
	 * @param target
	 *            查找目标
	 * @param from
	 *            当前查找的范围起�?
	 * @param to
	 *            当前查找的返回终�?
	 * @return 返回目标在数组中的索引位置�?如果不在数组中返回：NIL
	 */
	private static String binarySearch(int[] input, int target, int from, int to) {
		int range = to - from;
		// 如果范围大于0，即存在两个以上的元素，则继续拆�?
		if (range > 0) {
			// 选定中间�?
			// int mid = (to + from) / 2;
			int mid = from + (to - from) / 2;
			// 先判断两个二分的临界�?
			if (input[mid] == target) {
				return String.valueOf(mid);
			}
			// 如果临界位不满足，则继续二分查找
			if (input[mid] > target) {
				return binarySearch(input, target, from, mid - 1);
			} else {
				return binarySearch(input, target, mid + 1, to);
			}
		} else {
			if (input[from] == target) {
				return String.valueOf(from);
			} else {
				return "NIL";
			}
		}
		/*
		 * 复杂度分析： 在最坏情况下，一共要进行lgn次二分，可以确定�?��结果。�?每次二分中只进行二分和�?的比�?
		 * 等时间为常量的操作�?因此，二分查找的时间复杂度为：�?lg n) 公式表示如下�?
		 * 设数组长度是n的时候，时间为T(n)，则如果拆分成n/2长度的数组的时�?，需要的时间为T(n/2)，切
		 * 无需合并，拆分需要时间为c，为常量。所以T(n)=T(n/2)+c，T(n/2)=T(n/4)+c...
		 * �?��，T(n)=c+c+c+...，攻击lgn个c。所以时间复杂副为：Θ(lg n)
		 */
	}
}
