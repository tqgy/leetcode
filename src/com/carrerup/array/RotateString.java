package com.carrerup.array;

public class RotateString {

	private static char[] reverse(char[] chars, int begin, int end) {
		while (begin < end) {
			char tmp = chars[begin];
			chars[begin] = chars[end];
			chars[end] = tmp;
			begin++;
			end--;
		}
		return chars;
	}

	public static char[] rotate(char[] c, int n) {
		if (c == null || c.length < 2)
			return c;
		if (n > c.length)
			n = n % c.length;
		reverse(c, 0, n - 1);
		reverse(c, n, c.length - 1);
		reverse(c, 0, c.length - 1);
		return c;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String s = "abcdefg";
		String r = new String(rotate(s.toCharArray(), 4));
		System.out.println(r);
	}

}
