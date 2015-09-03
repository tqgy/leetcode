package com.carrerup.recursion;

/**
 * @author guyu
 */
public class Fibonacci {

	public static int fibonacci(int n) {
		int one = 0;
		int two = 1;
		int total = 0;
		if (n < 0) {
			return -1;
		} else if (n == 0) {
			return 0;
		} else if (n == 1) {
			return 1;
		} else {
			for (int i = 2; i <= n; i++) {
				total = one + two;
				one = two;
				two = total;
			}
			return total;
		}
	}

	public static int rFibonacci(int n) {
		if (n < 0) {
			return -1;
		} else if (n == 0) {
			return 0;
		} else if (n == 1) {
			return 1;
		} else {
			return rFibonacci(n - 1) + rFibonacci(n - 2);
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(fibonacci(2));
		System.out.println(fibonacci(3));
		System.out.println(fibonacci(4));
		System.out.println(fibonacci(5));
		System.out.println(fibonacci(6));
		System.out.println(fibonacci(7));
		System.out.println(fibonacci(8));
		System.out.println(fibonacci(9));

		System.out.println(rFibonacci(2));
		System.out.println(rFibonacci(3));
		System.out.println(rFibonacci(4));
		System.out.println(rFibonacci(5));
		System.out.println(rFibonacci(6));
		System.out.println(rFibonacci(7));
		System.out.println(rFibonacci(8));
		System.out.println(rFibonacci(9));
	}

}
