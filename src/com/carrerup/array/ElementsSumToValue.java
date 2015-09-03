package com.carrerup.array;

import java.util.ArrayList;

public class ElementsSumToValue {

	public static void findElemtsThatSumTo(int data[], int k) {

		ArrayList<Integer> arrayK[] = new ArrayList[k + 1];
		for (int i = 0; i < arrayK.length; i++)
			arrayK[i] = new ArrayList<Integer>();

		for (int i = 0; i < data.length; i++) {
			if (data[i] <= k)
				arrayK[data[i]].add(i);
		}

		for (int i = 0; i < arrayK.length / 2; i++) {
			if (!arrayK[i].isEmpty() && !arrayK[k - i].isEmpty()) {
				for (Object index : arrayK[i])
					for (Object otherIndex : arrayK[k - i])
						System.out.println("Numbers at indeces ["
								+ index.toString() + ", "
								+ otherIndex.toString() + "] add up to " + k
								+ ".");
			}
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] a = { 1, 12, 31, 34, 5, 16, 27, 18, 9, 60 };
		findElemtsThatSumTo(a, 65);
	}

}
