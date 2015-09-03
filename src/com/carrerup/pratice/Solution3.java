package com.carrerup.pratice;

public class Solution3 {

	static int get_the_count_of_shortest_ways_in_matrix(
			int[] matrix_into_array_with_road_blocker, int matrix_height,
			int matrix_width, int m, int n) {
		// Build the input matrix
		if (matrix_into_array_with_road_blocker == null
				|| matrix_height == 0
				|| matrix_width == 0
				|| matrix_into_array_with_road_blocker.length != matrix_height
						* matrix_width || m < 0 || n < 0 || m > matrix_height
				|| n > matrix_width) {
			// wrong parameter
			return 0;
		}

		int[][] count = new int[matrix_height][matrix_width];
		int[][] matrix = new int[matrix_height][matrix_width];
		for (int i = 0; i < matrix_into_array_with_road_blocker.length; i += matrix_width) {
			// System.out.println("i=" + i);
			for (int j = 0; j < matrix_width; j++) {
				matrix[i / matrix_width][j] = matrix_into_array_with_road_blocker[i
						+ j];
			}
		}

		for (int i = 0; i < matrix_height; ++i) {
			if (matrix[i][0] != 1)
				count[i][0] = 1;
			else
				break;
		}

		for (int j = 0; j < matrix_width; ++j) {
			if (matrix[0][j] != 1)
				count[0][j] = 1;
			else
				break;
		}

		if (matrix_height == 1) {
			for (int i = 0; i < n; i++) {
				if (matrix[0][i] == 1)
					return 0;
			}
			return 1;
		}

		if (matrix_width == 1) {
			for (int i = 0; i < m; i++) {
				if (matrix[i][0] == 1)
					return 0;
			}
			return 1;
		}
		for (int i = 1; i < matrix_height; ++i) {
			for (int j = 1; j < matrix_width; ++j) {
				if (matrix[i][j] != 1)
					count[i][j] = count[i - 1][j] + count[i][j - 1];
			}
		}
		return count[m][n];

	}

	static int getNumOfPaperToRead(int[] a, int t, int j) {
		if (a == null || a.length == 0 || j < 0 || j > a.length || t <= 0) {
			return 0;
		}
		int sum = 0;
		int cont = 0;
		// j is the nth book, so the index should be j-1
		for (int i = j - 1; i < a.length; i++) {
			sum += a[i];
			cont += a[i];
			// if read time greater than 10, the sleep 1 min
			if (cont >= 10) {
				sum++;
				cont = sum % 10;
			}
			if (sum > t)
				return i - j + 1;

		}
		return a.length - j + 1;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] a = { 0, 0, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0 };
		int t = 4;
		int j = 2;
		int r1 = getNumOfPaperToRead(a, t, j);
		System.out.println(r1);
		int r2 = get_the_count_of_shortest_ways_in_matrix(a, 4, 5, 3, 4);
		System.out.println(r2);
	}

}
