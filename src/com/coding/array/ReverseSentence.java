package com.carrerup.array;

public class ReverseSentence {

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

	private static boolean isChar(char c) {
		if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
			return true;
		} else {
			return false;
		}
	}

	public static char[] reverseSentence(char[] s) {
		if (s == null || s.length < 2)
			return s;
		reverse(s, 0, s.length - 1);
		System.out.println(new String(s));
		int start = 0, end = 0;
		while (start < s.length - 1) {
			if (!isChar(s[start])) {
				start++;
				end++;
			} else if (!isChar(s[end])) {
				reverse(s, start, end - 1);
				start = end;
			} else if (end == s.length - 1) {
				reverse(s, start, end);
				start = end;
			} else {
				end++;
			}
		}
		return s;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String s = "king is out ";

		System.out.println(new String(reverseSentence(s.toCharArray())));

	}

}
