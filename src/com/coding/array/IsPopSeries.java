package com.carrerup.array;

import java.util.Scanner;
import java.util.Stack;
import java.util.StringTokenizer;

public class IsPopSeries {

	/**
	 * As you know, two operations of Stack are push and pop. Now give you two
	 * integer arrays, one is the original array before push and pop operations,
	 * the other one is the result array after a series of push and pop
	 * operations to the first array. Please give the push and pop operation
	 * sequence.
	 * 
	 * For example:
	 * 
	 * If the original array is a[] = {1,2,3}, and the result array is b[] =
	 * {1,3,2}.
	 * 
	 * Then, the operation sequence is
	 * “push1|pop1|push2|push3|pop3|pop2�?operations are split by ‘|�?and no
	 * space).
	 * 
	 * Rules:
	 * 
	 * 1.The push and pop operations deal with the original int array from left
	 * to right.. 2.The input is two integer array. They are the original array
	 * and the result array. These interger array is split by space.. 3.The
	 * output is the operation sequence.. 4.If the original array cannot make to
	 * the result array with stack push and pop, The output should be 'None'..
	 * 5.The operation "push1" means push the first element of the original
	 * array to the stack.. 6.The operation "pop1" means pop the first element
	 * of the original array from the stack, and add this element to the tail of
	 * the result array.. 7.Please don't include any space in the output
	 * string..
	 * 
	 * Sample1: Input: 1 2 3 4
	 * 
	 * 1 2 3 4 Output: push1|pop1|push2|pop2|push3|pop3|push4|pop4
	 * 
	 * Sample2:
	 * 
	 * Input:
	 * 
	 * 1 2 3 4
	 * 
	 * 4 3 2 1 Output: push1|push2|push3|push4|pop4|pop3|pop2|pop1
	 */

	public static String generateSequence(int[] input, int[] output) {// o为初始数组，r为结果数�?
		String s = "";
		int i = 0;
		int j = 0;
		int len = input.length;
		Stack<Integer> stack = new Stack<Integer>();

		while (i < len && j < len) {
			if (stack.empty()) {
				stack.push(input[i]);
				s += "push" + input[i] + "|";
				i++;
			} else {
				if (stack.peek() != output[j]) {
					stack.push(input[i]);
					s += "push" + input[i] + "|";
					i++;
				} else {
					s += "pop" + stack.pop() + "|";
					j++;
				}
			}
		}
		while (j < len) {
			if (stack.peek() == output[j]) {
				s += "pop" + stack.pop() + "|";
				j++;
			} else {
				s = "None|";
				break;
			}
		}
		return s;
	}

	public static void main(String[] args) {

		Scanner scanner = new Scanner(System.in);

		while (scanner.hasNextLine()) {
			String strLine1 = scanner.nextLine();
			StringTokenizer stringTokenizer1 = new StringTokenizer(strLine1);

			// Initialize the original array
			int arrayLength = stringTokenizer1.countTokens();
			int[] originalArray = new int[arrayLength];
			for (int i = 0; i < arrayLength; i++) {
				originalArray[i] = Integer.parseInt(stringTokenizer1
						.nextToken());
			}

			// Initialize the result array
			String strLine2 = scanner.nextLine();
			StringTokenizer stringTokenizer2 = new StringTokenizer(strLine2);
			arrayLength = stringTokenizer2.countTokens();
			int[] resultArray = new int[arrayLength];
			for (int j = 0; j < arrayLength; j++) {
				resultArray[j] = Integer.parseInt(stringTokenizer2.nextToken());
			}

			String operationSequence = generateSequence(originalArray,
					resultArray);
			System.out.println(operationSequence);
		}

		// TODO Auto-generated method stub
		int[] input = { 1, 2, 3, 4 };
		int[] output = { 2, 4, 3, 1 };
		String s = generateSequence(input, output);
		System.out.println(s);
	}

}
