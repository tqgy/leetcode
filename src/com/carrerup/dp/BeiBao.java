package com.carrerup.dp;

/**
 * 
 */
public class BeiBao {
	static int[] a = new int[5]; // ��������
	static int[] b = new int[5]; // �������?
	static int flag = 0; // ��һ����ѡ��

	static int bound = 20; // ������
	static int totle = 0; // ÿ��ѡ����������

	public static void inserttry(int i, int leftbound, int t) {
		if (i < 5 && leftbound <= totle) {
			if (a[i] < leftbound) {
				b[t++] = a[i];
				totle = totle - a[i];

				leftbound = leftbound - a[i];
				i++;
				inserttry(i, leftbound, t);
			} else if (a[i] > leftbound) {
				totle = totle - a[i];
				i++;
				inserttry(i, leftbound, t);
			} else {
				b[t] = a[i];
				return;
			}
		} else {
			leftbound = leftbound + b[--t];

			for (int f = 0; f < 5; f++) {
				if (a[f] == b[t]) {
					flag = ++f;
					break;
				}
			}

			b[t] = 0;

			totle = 0;
			for (int m = flag; m < 5; m++) {
				totle += a[m];
			}
			inserttry(flag, leftbound, t);
		}
		return;
	}

	public static void main(String[] args) {
		a[0] = 11;
		a[1] = 8;
		a[2] = 6;
		a[3] = 7;
		a[4] = 5;

		for (int i = 0; i < 5; i++) {
			b[i] = 0;
		}
		for (int i = 0; i < 5; i++) {
			totle += a[i];
		}

		inserttry(0, 20, 0);
		for (int i = 0; i < 5; i++) {
			System.out.println(b[i]);
		}
	}
}
