package com.careerup.mango;

import java.util.*;
import java.util.concurrent.locks.*;

public class ConnectionPool {

    private final int maxSize;
    private final Queue<Connection> pool = new ArrayDeque<>();
    private final Lock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();
    private volatile boolean closed = false;

    public ConnectionPool(int maxSize) {
        this.maxSize = maxSize;
        for (int i = 0; i < maxSize; i++) {
            pool.add(new Connection()); // lazy open or eager open both OK
        }
    }

    // Acquire a connection (blocks if none available)
    public Connection acquire() throws InterruptedException {
        lock.lock();
        try {
            while (pool.isEmpty() && !closed) {
                // if need timeout, use awaitNanos()
                // notEmpty.awaitNanos(timeout)
                notEmpty.await();
            }
            if (closed) {
                throw new IllegalStateException("Pool is closed");
            }
            Connection conn = pool.poll();
            conn.open(); // ensure it's ready
            return conn;
        } finally {
            lock.unlock();
        }
    }

    // Release a connection back to pool
    // shouldRebuild = true means the client wants a fresh connection
    public void release(Connection conn, boolean shouldRebuild) {
        lock.lock();
        try {
            // if need prevent double release, use atomic reference
            // if (!conn.inUse.compareAndSet(true, false)) { 
            //      throw new IllegalStateException("Double release detected"); 
            // }
            if (closed) {
                conn.close();
                return;
            }

            if (shouldRebuild) {
                conn.close();
                conn = new Connection(); // rebuild
            }

            pool.offer(conn);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    // Close entire pool
    public void close() {
        lock.lock();
        try {
            closed = true;
            for (Connection conn : pool) {
                conn.close();
            }
            pool.clear();
            notEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }

    // -----------------------------
    // Mock Connection implementation
    // -----------------------------
    public static class Connection {
        private boolean opened = false;

        public void open() {
            if (!opened) {
                // simulate open
                opened = true;
            }
        }

        public void runCommand(String cmd) {
            if (!opened) throw new IllegalStateException("Not opened");
            // simulate running command
        }

        public void close() {
            if (opened) {
                // simulate close
                opened = false;
            }
        }
    }
}

