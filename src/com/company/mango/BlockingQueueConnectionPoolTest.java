package com.careerup.mango;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class BlockingQueueConnectionPoolTest {
    public static void main(String[] args) {
        try {
            testBasicAcquireRelease();
            testTimeout();
            testDoubleRelease();
            testMultiThreaded();
            System.out.println("ALL BLOCKING QUEUE TESTS PASSED");
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void testBasicAcquireRelease() throws Exception {
        System.out.println("Running testBasicAcquireRelease...");
        BlockingQueueConnectionPool pool = new BlockingQueueConnectionPool(2);
        BlockingQueueConnectionPool.Connection c1 = pool.acquire();
        
        // pool is empty
        BlockingQueueConnectionPool.Connection c3 = pool.acquire(100, TimeUnit.MILLISECONDS);
        if (c3 != null) throw new RuntimeException("Should have timed out");
        
        pool.release(c1, false);
        BlockingQueueConnectionPool.Connection c4 = pool.acquire();
        if (c4 != c1) throw new RuntimeException("Should have gotten released connection");
    }

    private static void testDoubleRelease() throws Exception {
        System.out.println("Running testDoubleRelease...");
        BlockingQueueConnectionPool pool = new BlockingQueueConnectionPool(1);
        BlockingQueueConnectionPool.Connection c1 = pool.acquire();
        pool.release(c1, false);
        try {
            pool.release(c1, false);
            throw new RuntimeException("Should have thrown IllegalStateException for double release");
        } catch (IllegalStateException e) {
            // expected
        }
    }

    private static void testTimeout() throws Exception {
        System.out.println("Running testTimeout...");
        BlockingQueueConnectionPool pool = new BlockingQueueConnectionPool(1);
        pool.acquire();
        BlockingQueueConnectionPool.Connection c = pool.acquire(100, TimeUnit.MILLISECONDS);
        if (c != null) throw new RuntimeException("Should return null on timeout");
    }

    private static void testMultiThreaded() throws Exception {
        System.out.println("Running testMultiThreaded...");
        final int POOL_SIZE = 5;
        final int THREADS = 20;
        final BlockingQueueConnectionPool pool = new BlockingQueueConnectionPool(POOL_SIZE);
        final AtomicInteger errors = new AtomicInteger(0);
        
        Runnable task = () -> {
            try {
                BlockingQueueConnectionPool.Connection c = pool.acquire(1, TimeUnit.SECONDS);
                if (c == null) {
                    return;
                }
                // Simulate use
                Thread.sleep(10);
                pool.release(c, false);
            } catch (Exception e) {
                e.printStackTrace();
                errors.incrementAndGet();
            }
        };

        Thread[] threads = new Thread[THREADS];
        for (int i = 0; i < THREADS; i++) {
            threads[i] = new Thread(task);
            threads[i].start();
        }
        for (Thread t : threads) t.join();
        
        if (errors.get() > 0) throw new RuntimeException("Errors in multithreaded test");
    }
}
