package com.carrerup.dp;

import java.util.ArrayList;
import java.util.List;

public class LongestCommonSubsequence {

	/** 
     * 
     */
	public static void main(String[] args) {
		LongestCommonSubsequence lcs = new LongestCommonSubsequence();
		String[] str = { "BDCABA", "ABCBDAB", "abcd", "ad" };
		// test "BDCABA","ABCBDAB"
		int length = lcs.findLCS(str[0], str[1]);
		System.out.println(length);
		System.out.println(lcs.list);

		lcs.list.clear();

		// test "abcd","ad"
		length = lcs.findLCS(str[2], str[3]);
		System.out.println(length);
		System.out.println(lcs.list);
	}

	/*
	 * / 0 if i<0 or j<0 //case 1 c[i,j]= c[i-1,j-1]+1 if i,j>=0 and xi=xj
	 * //case 2 \ max(c[i,j-1],c[i-1,j] if i,j>=0 and xi��xj //case 3
	 */
	public int findLCS(String strA, String strB) {
		if (strA == null || strB == null) {
			return 0;
		}
		int len1 = strA.length();
		int len2 = strB.length();
		if (len1 == 0 || len2 == 0) {
			return 0;
		}

		char[] lettersA = strA.toCharArray();
		char[] lettersB = strB.toCharArray();

		int[][] LCS_Length = new int[len1][len2];
		// 'direction' records how lcs[i][j] come from.It's used to print LCS.
		Direction[][] direction = new Direction[len1][len2];

		for (int i = 0; i < len1; i++) {
			for (int j = 0; j < len2; j++) {
				if (i == 0 || j == 0) {// case 1
					if (lettersA[i] == lettersB[j]) {
						LCS_Length[i][j] = 1;
						direction[i][j] = Direction.LeftUp;
					} else {
						if (i > 0) {
							LCS_Length[i][j] = LCS_Length[i - 1][j];
							direction[i][j] = Direction.Up;
						}
						if (j > 0) {
							LCS_Length[i][j] = LCS_Length[i][j - 1];
							direction[i][j] = Direction.Left;
						}

					}
				} else {
					if (lettersA[i] == lettersB[j]) {// case 2
						LCS_Length[i][j] = LCS_Length[i - 1][j - 1] + 1;
						direction[i][j] = Direction.LeftUp;
					} else {// case 3
						if (LCS_Length[i][j - 1] > LCS_Length[i - 1][j]) {
							LCS_Length[i][j] = LCS_Length[i][j - 1];
							direction[i][j] = Direction.Left;
						} else {
							LCS_Length[i][j] = LCS_Length[i - 1][j];
							direction[i][j] = Direction.Up;
						}
					}
				}
			}
		}
		printLCS(direction, lettersA, lettersB, len1 - 1, len2 - 1);
		return LCS_Length[len1 - 1][len2 - 1];
	}

	private List<Character> list = new ArrayList<Character>();

	// from lcs[len-1][len-1] to lcs[0][0]
	public void printLCS(Direction[][] direction, char[] lettersA,
			char[] lettersB, int row, int col) {
		if (lettersA == null || lettersB == null) {
			return;
		}
		int len1 = lettersA.length;
		int len2 = lettersB.length;
		if (len1 == 0 || len2 == 0 || !(row < len1 && col < len2)) {
			return;
		}
		if (direction[row][col] == Direction.LeftUp) {
			if (row > 0 && col > 0) {
				printLCS(direction, lettersA, lettersB, row - 1, col - 1);
			}
			list.add(lettersA[row]);
		}
		if (direction[row][col] == Direction.Left) {
			if (col > 0) {
				printLCS(direction, lettersA, lettersB, row, col - 1);
			}
		}
		if (direction[row][col] == Direction.Up) {
			if (row > 0) {
				printLCS(direction, lettersA, lettersB, row - 1, col);
			}
		}

	}

	public enum Direction {
		Up, Left, LeftUp
	};
}