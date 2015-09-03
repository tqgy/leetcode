package com.carrerup.array;

public class RemoveDuplicate {

	private static String removeSortedDup(String s) {
		char[] chars = s.toCharArray();
		int i = 0;
		int j;
		if (chars.length <= 1)
			return s;

		for (j = 1; j < chars.length; j++)
			if (chars[j] != chars[i])
				chars[++i] = chars[j];

		return new String(chars);
	}

	public static char[] removeDuplicatesEff(char[] str) {
		if (str == null)
			return null;
		int len = str.length;
		if (len < 2)
			return null;
		boolean[] hit = new boolean[256];
		for (int i = 0; i < 256; ++i) {
			hit[i] = false;
		}
		hit[str[0]] = true;
		int tail = 1;
		for (int i = 1; i < len; ++i) {
			if (!hit[str[i]]) {
				str[tail] = str[i];
				++tail;
				hit[str[i]] = true;
			}
		}
		str[tail] = 0;
		return str;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String s = "aahhbjnvvbbh";
		String sort = "aaabbdddfffggg";
		System.out.println(removeSortedDup(sort));
		System.out.println(removeDuplicatesEff(s.toCharArray()));
	}

}
