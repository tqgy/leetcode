package com.carrerup.concurrent;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author guyu
 */
public class MyThread implements Runnable {

	volatile String myString = "Yes ";

	public void run() {
		this.myString = "No ";
	}

	public static void main(String[] args) {

		MyThread t = new MyThread();
		new Thread(t).start();

		for (int i = 0; i < 10; i++)
			System.out.print(t.myString);

		AtomicInteger[][] counts = new AtomicInteger[10][2];
		for (int i = 0; i < 10; i++)
			for (int j = 0; j < 2; j++)
				counts[i][j] = new AtomicInteger(0);
		Thread[] threads = new Thread[1000];
		for (int i = 0; i < 1000; i++) {
			t = new MyThread();
			threads[i] = new Thread(t);
			threads[i].start();

			for (int j = 0; j < 10; j++) {
				if ("Yes".equals(t.myString))
					counts[j][0].incrementAndGet();
				else
					counts[j][1].incrementAndGet();
			}
		}

		try {
			for (int i = 0; i < 1000; i++)
				threads[i].join();
		} catch (Exception e) {
			System.out.println(e);
		}

		for (int i = 0; i < 10; i++) {
			System.out.println("Round " + i + " Yes=" + counts[i][0].get()
					+ ",No=" + counts[i][1].get());
		}
	}
}
