package com.carrerup.sort;

public class QuickSort {

	// 待排数组
	private static int[] input = new int[] { 2, 1, 5, 4, 9, 8, 6, 7, 10, 3 };

	public static void main(String[] args) {
		// 快�?排序
		quickSort(0, input.length - 1);
		// qSort(1, input.length - 1);
		// 打印数组
		printArray();

	}

	/**
	 * 快�?排序，伪代码�?QUICKSORT(A, p, r) 1 if p < r 2 then q �?PARTITION(A, p, r) 3
	 * QUICKSORT(A, p, q - 1) 4 QUICKSORT(A, q + 1, r)
	 * 
	 * PARTITION(A, p, r) 1 x �?A[r] 2 i �?p - 1 3 for j �?p to r - 1 4 do if
	 * A[j] �?x 5 then i �?i + 1 6 exchange A[i] �?A[j] 7 exchange A[i + 1] �?
	 * A[r] 8 return i + 1 复杂度，�?��情况下：Θ(n^2) �?��平衡情况：�?nlgn)
	 * 
	 * @param array
	 *            待排数组
	 * @param from
	 *            起始位置
	 * @param to
	 *            终止位置
	 */
	private static void quickSort(int start, int end) {
		if (start < end) {
			int key = input[start];
			int k = start;
			for (int i = start + 1; i <= end; i++) {
				if (input[i] < key) {
					k++;
					int temp = input[i];
					input[i] = input[k];
					input[k] = temp;
				}
			}
			input[start] = input[k];
			input[k] = key;
			quickSort(start, k);
			quickSort(k + 1, end);
		}
	}

	private static void qSort(int low, int high) {
		if (low < high) {
			int key = input[high];
			int i = low - 1;
			for (int j = low; j < high; j++) {
				if (input[j] <= key) {
					i++;
					int temp = input[j];
					input[j] = input[i];
					input[i] = temp;
				}
				input[high] = input[i + 1];
				input[i + 1] = key;
			}
			qSort(low, i - 1);
			qSort(i + 1, high);
		}
	}

	private static void printArray() {
		for (int i : input) {
			System.out.print(i + " ");
		}
	}

}