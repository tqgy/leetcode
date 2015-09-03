package com.carrerup.recursion;

import java.util.Iterator;
import java.util.LinkedList;

public class SeperateNumber {
	/**
	 * 题目如下�?每个大于1的数字都可以由其他数字相加组合�?成，例如�?5=1+4, 5=2+3�?7=1+6, 7=2+5, 7=3+4�?
	 * 现要求输入正整数n（n<50），输出�?��除n本身外正数相加的不重复组�?【例如�? 输入�?8 输出�?8=1+2+5 8=1+3+4 8=1+7
	 * 8=2+6 8=3+5
	 */
	public static LinkedList<Integer> stack = new LinkedList();

	public static void seperate(int num, int sum, int level) {
		if (num == sum) {
			// find the path, print stack
			for (Iterator iterator = stack.iterator(); iterator.hasNext();) {
				int type = (Integer) iterator.next();
				System.out.print(" " + type);
			}
			System.out.println("");
		} else if (level == num || sum > num) {
			return;
		} else {
			stack.addLast(level);
			seperate(num, sum + level, level + 1);
			stack.removeLast();
			seperate(num, sum, level + 1);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		seperate(10, 0, 1);
	}

}
