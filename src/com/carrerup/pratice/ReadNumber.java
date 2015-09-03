package com.carrerup.pratice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/*
 *
 情况A：以0�?9为例，在英语中直接描述�?

 情况B：以99为例，首先描�?0，然后再描述9，进入情况A�?

 情况C：以999为例，首先描�?00，然后再描述99，进入情况B�?

 情况D：以999,999为例，想要用英语描述这个数字，首先需要描�?99,000，英语描�?99,000时，先描�?99，进入情况C，然后加�?��thousand；然后再描述999，进入情况C�?
 6.2 使用递归解决问题

 第一步，尽量�?��问题的表述�?用英语描述一个正整数n，n属于0 - 999,999�?

 第二步，确定停止情况。n属于{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19}�?

 第三步，递归步骤。在该问题中�?��按n的不同情况来考虑�?

 n>=20 && n<=99�?

 n>=100 && n<=999�?

 n>=1000 && n<=999,999�?
 */
public class ReadNumber {

	private static void printNumber(int num) {

		// 0-19

		switch (num) {

		case 0:
			System.out.print("zero");
			return;

		case 1:
			System.out.print("one");
			return;

		case 2:
			System.out.print("two");
			return;

		case 3:
			System.out.print("three");
			return;

		case 4:
			System.out.print("four");
			return;

		case 5:
			System.out.print("five");
			return;

		case 6:
			System.out.print("six");
			return;

		case 7:
			System.out.print("senven");
			return;

		case 8:
			System.out.print("eight");
			return;

		case 9:
			System.out.print("nine");
			return;

		case 10:
			System.out.print("ten");
			return;

		case 11:
			System.out.print("elevent");
			return;

		case 12:
			System.out.print("twelve");
			return;

		case 13:
			System.out.print("thirteen");
			return;

		case 14:
			System.out.print("fourteen");
			return;

		case 15:
			System.out.print("fifteen");
			return;

		case 16:
			System.out.print("sixteen");
			return;

		case 17:
			System.out.print("seventeen");
			return;

		case 18:
			System.out.print("eighteen");
			return;

		case 19:
			System.out.print("nighteen");
			return;

		default:
			;// >=20

		}

		// 20-99

		if (num / 10 < 10) {

			switch (num / 10) {

			case 2:
				System.out.print("twenty");
				break;

			case 3:
				System.out.print("thirty");
				break;

			case 4:
				System.out.print("fourty");
				break;

			case 5:
				System.out.print("fifty");
				break;

			case 6:
				System.out.print("sixty");
				break;

			case 7:
				System.out.print("seventy");
				break;

			case 8:
				System.out.print("eighty");
				break;

			case 9:
				System.out.print("ninety");
				break;

			default:
				break;

			}

			if (num % 10 != 0) {

				System.out.print(" ");
				printNumber(num % 10);

			}

			return;

		}

		// 100-999

		if (num / 100 < 10) {

			printNumber(num / 100);

			System.out.print(" hundred");

			if (num % 100 != 0) {

				System.out.print(" and ");
				printNumber(num % 100);

			}

			return;

		}

		// 1000-999,999

		printNumber(num / 1000);

		System.out.print(" thousand");

		if (num % 1000 != 0) {

			System.out.print(" ");
			printNumber(num % 1000);

		}

		return;

	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		InputStreamReader isr = new InputStreamReader(System.in);

		BufferedReader br = new BufferedReader(isr);

		int num = Integer.parseInt(br.readLine());

		printNumber(num);

		isr.close();

		br.close();
	}

}
