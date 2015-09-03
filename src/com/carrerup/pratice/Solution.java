package com.carrerup.pratice;

import java.util.Iterator;
import java.util.Vector;

public class Solution {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int S = 11;
		int v = 1;
		System.out.println(S / v);

	}

	static int minCoins(Vector<Integer> a, int S) {
		int k = a.size();
		//System.out.println("k: " + k);
		int[] v = new int[a.size()];
		int m = 0;
		for (Iterator iterator = a.iterator(); iterator.hasNext();) {
			v[m] = (Integer) iterator.next();
			//System.out.println("v[" + m + "] : " + v[m]);
			m++;
		}
		// Sort, Big to small
		for (int i = 0; i < v.length - 1; i++) {
			int key = v[i];
			int index = i;

			for (int j = i + 1; j < v.length; j++) {
				key = key > v[j] ? key : v[j];
				index = key > v[j] ? index : j;
			}
			v[index] = v[i];
			v[i] = key;
		}

		int[] num = new int[k];

		for (int i = 0; i < k; i++) {
			//number of coins
			num[i] = S / v[i];
			//System.out.println("num[ " + i + "] : " + num[i]);
			S = S % v[i];
			//System.out.println("S after: " + S);
		}

		int number = 0;
		for (int i = 0; i < num.length; i++) {
			number += num[i];
		}
		if (S != 0) {
			return -1;
		} else {
			return number;
		}
	}
}
