/**
 * 
 */
package com.carrerup.array;

import java.util.Stack;

/**
 * @author guyu
 * 
 */
public class ReverseString {

	public static String reverseString(String s) {

		if (s == null) {
			return null;
		}

		if (s.length() == 0) {
			return "";
		}

		char[] chars = s.toCharArray();
		for (int i = 0; i < chars.length / 2; i++) {
			char tmp = chars[i];
			chars[i] = chars[chars.length - i - 1];
			chars[chars.length - i - 1] = tmp;
		}

		return new String(chars);
	}

	public static char[] reverse(char[] s) {
		if (s == null || s.length == 0) {
			return null;
		}
		int len = s.length;

		for (int i = 0; i < len / 2; i++) {
			char temp = s[i];
			s[i] = s[len - i - 1];
			s[len - i - 1] = temp;
		}
		return s;
	}

	public static boolean isChar(char c) {
		if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))
			return true;
		else
			return false;
	}

	public static String reverseWord(String words) {
		if (words == null || words.equalsIgnoreCase(""))
			return "";
		else {
			char[] chs = words.toCharArray();
			// System.out.println(new String(chs));
			Stack stack = new Stack();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < chs.length; i++) {
				if (isChar(chs[i])) {
					stack.push(chs[i]);
				} else {
					while (stack.size() > 0) {
						sb.append(stack.pop());
					}
					sb.append(chs[i]);
				}

			}
			if (stack.size() > 0) {
				while (stack.size() > 0) {
					sb.append(stack.pop());
				}
			}

			return sb.toString();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String s = "Hello ! world! ...con";
		System.out.println(reverseString(s));

		System.out.println(reverseWord(s));
	}

}
