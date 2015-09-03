package com.carrerup.array;

import java.util.LinkedList;

public class FirstExistOnce {

	public static char firstOnce(char[] chars) {
		if (chars == null)
			return ' ';
		if (chars.length < 2)
			return chars[0];
		LinkedList list = new LinkedList();
		int[] map = new int[256];
		for (int i = 0; i < 256; i++) {
			map[i] = 0;
		}
		for (int i = 0; i < chars.length; i++) {
			if (map[chars[i]] == 0) {
				map[chars[i]]++;
				list.add(chars[i]);
			} else if (map[chars[i]] == 1) {
				map[chars[i]]++;
				list.remove((Character) chars[i]);
			}
		}
		if (list.size() == 0) {
			return ' ';
		}
		return (Character) list.get(0);

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String s = "aabcdefcefgb";
		System.out.println(firstOnce(s.toCharArray()));
	}

}
