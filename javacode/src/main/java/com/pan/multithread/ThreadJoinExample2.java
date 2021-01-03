package com.pan.multithread;

import java.math.BigDecimal;


public class ThreadJoinExample2 {
	public static void main(String[] args) throws InterruptedException {
		BigDecimal amountFee = BigDecimal.valueOf(Double.valueOf("199.95")).setScale(2, BigDecimal.ROUND_HALF_UP);
		System.out.println(amountFee.setScale(1, BigDecimal.ROUND_HALF_UP));
		System.out.println(amountFee);
		
		
		class Counter {
			private int count = 0;

			public synchronized void increment() {
				++count;
			}

			public int getCount() {
				return count;
			}
		}
		final Counter counter = new Counter();
		class CountingThread extends Thread {
			public void run() {
				for (int x = 0; x < 10000; ++x)
					counter.increment();
			}
		}
		CountingThread t1 = new CountingThread();
		CountingThread t2 = new CountingThread();
		t1.start();
		t2.start();
		t1.join(); //It will put the current thread(main) on wait until the thread on which it is called is dead.
		t2.join();
		System.out.println(counter.getCount());
	}
}