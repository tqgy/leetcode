package com.carrerup.pratice;

import java.util.Scanner;

public class InsertionChangeCount {

	private static void countChange(int num, String value) {

		String[] chars = value.split(" ");
		int[] a = new int[chars.length];
		for (int j = 0; j < chars.length; j++) {
			a[j] = Integer.parseInt(chars[j]);
		}
		int times = 0;
		
		for (int j = 1; j < num; j++) {
			int key = a[j];
			int i = j - 1;

			while (i >= 0 && a[i] > key) {
				a[i + 1] = a[i];
				a[i] = key;
				i--;
				times++;
			}
		}
		
		System.out.println(times);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner scan = new Scanner(System.in);
		
		String s = scan.nextLine();
		int n = Integer.parseInt(s);
		
		int[] numArray = new int[n];
		String[] vArray = new String[n];

		int i = 0;
		int j = 0;
		boolean isNum = true;
		
		while ((s = scan.nextLine()) != null) {
			if (isNum) {
				numArray[i] = Integer.parseInt(s);
				isNum = false;
				i++;
			} else {
				vArray[i - 1] = s;
				isNum = true;
			}
			
			j++;
			if (j == (2 * n)) {
				break;
			}
		}

		for (int k = 0; k < n; k++) {
			countChange(numArray[k], vArray[k]);
		}

	}

}
