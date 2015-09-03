package com.carrerup.pratice;

import java.util.Iterator;
import java.util.LinkedList;

public class CalculateSum {

	/**
	 * 题目：给定一个数组和�?��数字，请在这个数组的元素之间添加+-符号，使这个数组元素运算之后的结果是这个数字�?比如{2,3,4}1
	 * �?+3-4=1输出这样的等�?分析：这是亚马�?2013年在线笔试的�?��题目，题目不难，应该使用递归来做
	 * 若数组只有一个元素，则这个元素和�?��数字相等，则输出(元素=数字), 若和�?��元素不相等，则不输出，返�?
	 * 否则，使�?�?两种符号递归当前数组的第�?��元素
	 */
	public static LinkedList list = new LinkedList();

	public static void calculateSum(int result, int level, int sum, int[] input) {
		// System.out.println("------------------");
		// System.out.println("Level:" + level + " Sum:" + sum);
		// for (Iterator iterator = list.iterator(); iterator.hasNext();) {
		// String type = (String) iterator.next();
		// System.out.println(" " + type);
		// }
		// System.out.println("------------------");
		if (result == sum) {
			// find the path, print stack
			// list.removeLast();
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				String type = (String) iterator.next();
				System.out.print(" " + type);
			}
			System.out.println("");
		} else if (level >= input.length) {
			// System.out.println("wrong");
			// list.removeLast();
		} else {
			list.addLast("+");
			calculateSum(result, level + 1, sum + input[level], input);
			list.removeLast();
			list.addLast("-");
			calculateSum(result, level + 1, sum - input[level], input);
			list.removeLast();

		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] input = { 3, 4, 5, 6 };
		int result = 6;
		calculateSum(result, 1, input[0], input);
	}

}
