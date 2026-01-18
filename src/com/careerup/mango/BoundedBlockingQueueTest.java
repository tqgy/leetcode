package com.careerup.mango;

import java.util.concurrent.atomic.AtomicInteger;

public class BoundedBlockingQueueTest {

    public static void main(String[] args) {
        try {
            testBasicPutTake();
            testBlocking();
            testConcurrency();
            testFairness(); 
            System.out.println("ALL BOUNDED QUEUE TESTS PASSED");
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void testBasicPutTake() throws Exception {
        System.out.println("Running testBasicPutTake...");
        BoundedBlockingQueue<Integer> queue = new BoundedBlockingQueue<>(2);
        queue.put(1);
        queue.put(2);
        if (queue.size() != 2) throw new RuntimeException("Size wrong");
        if (queue.take() != 1) throw new RuntimeException("Order wrong");
        if (queue.take() != 2) throw new RuntimeException("Order wrong");
    }

    private static void testBlocking() throws Exception {
        System.out.println("Running testBlocking...");
        BoundedBlockingQueue<Integer> queue = new BoundedBlockingQueue<>(1);
        
        Thread t1 = new Thread(() -> {
            try {
                queue.put(1);
                queue.put(2); // Should block here
            } catch (InterruptedException e) {
                // ignore
            }
        });
        
        t1.start();
        Thread.sleep(100); // give time to block
        
        if (queue.size() != 1) throw new RuntimeException("Should cover 1 item");
        int val = queue.take();
        if (val != 1) throw new RuntimeException("Should take 1");
        
        t1.join(1000);
        if (t1.isAlive()) throw new RuntimeException("Producer stuck");
        if (queue.size() != 1) throw new RuntimeException("Size mismatch after unblock");
    }

    private static void testConcurrency() throws Exception {
        System.out.println("Running testConcurrency...");
        final int COUNT = 1000;
        final BoundedBlockingQueue<Integer> queue = new BoundedBlockingQueue<>(10);
        final AtomicInteger consumedCount = new AtomicInteger(0);
        
        Runnable producer = () -> {
            try {
                for (int i = 0; i < COUNT; i++) queue.put(i);
            } catch (InterruptedException e) { }
        };

        Runnable consumer = () -> {
            try {
                for (int i = 0; i < COUNT; i++) {
                    queue.take();
                    consumedCount.incrementAndGet();
                }
            } catch (InterruptedException e) { }
        };

        Thread p = new Thread(producer);
        Thread c = new Thread(consumer);
        p.start();
        c.start();
        p.join();
        c.join();

        if (consumedCount.get() != COUNT) throw new RuntimeException("Lost updates");
        if (queue.size() != 0) throw new RuntimeException("Queue not empty");
    }
    
    private static void testFairness() throws Exception {
        System.out.println("Running testFairness (smoke test)...");
        // Detailed fairness testing is hard, but we can verify it doesn't crash
        BoundedBlockingQueue<Integer> queue = new BoundedBlockingQueue<>(10, true);
        queue.put(1);
        if (queue.take() != 1) throw new RuntimeException("Fair queue failed basics");
    }
}
