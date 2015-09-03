package com.carrerup.pratice;

import java.util.ArrayList;

public class RandomGenerator {

	/*
	 * In Java, static double Math.random() returns a fair random double in the
	 * range [0,1)
	 */

	/*
	 * Using Math.random(), please write a function returning a fair random int
	 * in the range 0..99 inclusive.
	 */

	public static int getIntRandom() {
		double d = Math.random();
		return (int) (d * 100);
	}

	/*
	 * Using Math.random(), please write a function returning a fair random int
	 * in the range 0..99 inclusive such that once a number has been returned,
	 * it will never be returned again.
	 */
	static boolean[] hit = new boolean[100];
	static int counter = 0;
	static ArrayList<Integer> list = new ArrayList<Integer>();
	static {
		for (int i = 0; i < 100; i++) {
			list.add(i);
		}
	}

	public static int getUniqueRandomInt() throws Exception {
		int t = getIntRandom();
		if (counter == 100) {
			// counter=0;
			throw new Exception();
		}
		while (hit[t]) {
			t = getIntRandom();
		}
		hit[t] = true;
		counter++;
		return t;
	}

	/*
	 * Big-O worst case upper bound for one call to getUniqueRandomInt()? (where
	 * n is the size of the domain, 100 here) O(nn)
	 */

	public static int getUniqueRandomIntEff() throws Exception {
		if (list.size() == 0)
			throw new Exception();
		int t = getIntRandom();
		int r = list.get(t % list.size());
		list.remove(t % list.size());
		return r;
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		for (int i = 0; i < 10; i++) {
			getUniqueRandomIntEff();
		}
	}

}
