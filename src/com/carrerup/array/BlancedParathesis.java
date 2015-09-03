package com.carrerup.array;

public class BlancedParathesis {

	/**
	 * Implement a function string balanceParanthesis(string s); which given a
	 * string s consisting of some parenthesis returns a string s1 in which
	 * parenthesis are balanced and differences between s and s1 are minimum. Eg
	 * - "(ab(xy)u)2)" -> "(ab(xy)u)2" ")))(((" -> ""
	 */
	String balance(char[] input) {
		int par = 0;
		int len = input.length;
		String balanced = "";
		int last = len - 1;
		for (int i = 0; i < len; i++) {
			if (input[i] == ')') {
				if (par > 0) {
					par--;
					balanced += input[i];
				}
			} else if (input[i] == '(') {
				par++;
				while (i < last) {
					if (input[last] == ')') {
						balanced += input[i];
						break;
					}
					last--;
				}
			} else
				balanced += input[i];
		}
		return balanced;

	}

	public static void printPar(int l, int r, char[] str, int count) {
		if (l < 0 || r < l)
			return; // invalid state
		if (l == 0 && r == 0) {
			System.out.println(str); // found one, so print it
		} else {
			if (l > 0) { // try a left paren, if there are some available
				str[count] = '(';
				printPar(l - 1, r, str, count + 1);
			}
			if (r > l) { // try a right paren, if there’s a matching left
				str[count] = ')';
				printPar(l, r - 1, str, count + 1);
			}
		}
	}

	public static void printPar(int count) {
		char[] str = new char[count * 2];
		printPar(count, count, str, 0);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
