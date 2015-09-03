package com.carrerup.recursion;

import java.util.ArrayList;

public class Permutation {

	/**
	 * Write a method to compute all permutations of a string
	 */
	/*
	 * SOLUTION Letâ€™s assume a given string S represented by the letters A1, A2,
	 * A3, ... , An To permute set S, we can select the first character, A1,
	 * permute the remainder of the string to get a new list. Then, with that
	 * new list, we can â€œpushâ€?A1 into each possible position. For example, if
	 * our string is â€œabcâ€? we would do the following: 1. Let first = â€œaâ€?and
	 * let remainder = â€œbcâ€?2. Let list = permute(bc) = {â€œbcâ€? â€œcdâ€} 3. Push â€œaâ€?
	 * into each location of â€œbcâ€?(--> â€œabcâ€? â€œbacâ€? â€œbcaâ€? and â€œcbâ€?(--> â€œacbâ€?
	 * â€œcabâ€? â€œcbaâ€? 4. Return our new list
	 */
	public static ArrayList<String> getPerms(String s) {
		ArrayList<String> permutations = new ArrayList<String>();
		if (s == null) { // error case
			return null;
		} else if (s.length() == 0) { // base case
			permutations.add("");
			return permutations;
		}

		char first = s.charAt(0); // get the first character
		String remainder = s.substring(1); // remove the first character
		ArrayList<String> words = getPerms(remainder);
		for (String word : words) {
			for (int j = 0; j <= word.length(); j++) {
				permutations.add(insertCharAt(word, first, j));
			}
		}
		return permutations;
	}

	public static String insertCharAt(String word, char c, int i) {
		String start = word.substring(0, i);
		String end = word.substring(i);
		return start + c + end;
	}

	void permute(char[] perm, int level) {
		if (level == perm.length)
			System.out.println(new String(perm));

		for (int i = level; i < perm.length; i++) {
			if (level != i)
				swap(perm, level, i);
			permute(perm, level + 1);
			if (level != i)
				swap(perm, level, i);
		}
	}

	void swap(char[] arr, int i, int j) {

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("ï»¿å®¢æˆ·å§“å?.equals("å®¢æˆ·å§“å"));

	}

}
