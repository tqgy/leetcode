package com.careerup.mango;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionPoolTest {
    public static void main(String[] args) {
        try {
            testBasicAcquireRelease();
            testTimeout();
            testDoubleRelease();
            testMultiThreaded();
            System.out.println("ALL TESTS PASSED");
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void testBasicAcquireRelease() throws Exception {
        System.out.println("Running testBasicAcquireRelease...");
        ConnectionPool pool = new ConnectionPool(2);
        ConnectionPool.Connection c1 = pool.acquire();
        ConnectionPool.Connection c2 = pool.acquire();
        // pool is empty
        ConnectionPool.Connection c3 = pool.acquire(100, TimeUnit.MILLISECONDS);
        if (c3 != null) throw new RuntimeException("Should have timed out");
        
        pool.release(c1, false);
        ConnectionPool.Connection c4 = pool.acquire();
        if (c4 != c1) throw new RuntimeException("Should have gotten released connection");
    }

    private static void testDoubleRelease() throws Exception {
        System.out.println("Running testDoubleRelease...");
        ConnectionPool pool = new ConnectionPool(1);
        ConnectionPool.Connection c1 = pool.acquire();
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
        ConnectionPool pool = new ConnectionPool(1);
        pool.acquire();
        ConnectionPool.Connection c = pool.acquire(100, TimeUnit.MILLISECONDS);
        if (c != null) throw new RuntimeException("Should return null on timeout");
    }

    private static void testMultiThreaded() throws Exception {
        System.out.println("Running testMultiThreaded...");
        final int POOL_SIZE = 5;
        final int THREADS = 20;
        final ConnectionPool pool = new ConnectionPool(POOL_SIZE);
        final AtomicInteger errors = new AtomicInteger(0);
        
        Runnable task = () -> {
            try {
                ConnectionPool.Connection c = pool.acquire(1, TimeUnit.SECONDS);
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
