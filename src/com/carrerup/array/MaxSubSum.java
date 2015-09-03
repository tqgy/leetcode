package com.carrerup.array;

/*
 * 
 */
public class MaxSubSum {

	public static int getMaxSub(int[] array) {
		int startMax = array[0];
		int allMax = array[0];

		for (int i = 1; i < array.length; i++) {

			if (startMax < 0) {
				startMax = 0;
			}

			startMax += array[i];
			if (startMax > allMax) {

				allMax = startMax;

			}
		}
		return allMax;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] a = { -2, 5, 3, -6, 4, -8, 6 };
		System.out.println(getMaxSub(a));
	}

}
