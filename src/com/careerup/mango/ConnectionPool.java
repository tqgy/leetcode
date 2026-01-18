package com.careerup.mango;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
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
        Connection conn = null;
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
            conn = pool.poll();
            // Mark as in-use before releasing lock to ensure validity
            if (conn != null) {
                conn.inUse.set(true);
            }
        } finally {
            lock.unlock();
        }
        
        if (conn != null) {
            conn.open(); // open outside the lock
        }
        return conn;
    }

    // Acquire with timeout
    public Connection acquire(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        Connection conn = null;
        lock.lock();
        try {
            while (pool.isEmpty() && !closed) {
                if (nanos <= 0L) {
                    return null; // timed out
                }
                nanos = notEmpty.awaitNanos(nanos);
            }
            if (closed) {
                throw new IllegalStateException("Pool is closed");
            }
            conn = pool.poll();
            if (conn != null) {
                conn.inUse.set(true);
            }
        } finally {
            lock.unlock();
        }

        if (conn != null) {
            conn.open(); // open outside the lock
        }
        return conn;
    }

    // Release a connection back to pool
    // shouldRebuild = true means the client wants a fresh connection
    public void release(Connection conn, boolean shouldRebuild) {
        // Prevent double release
        if (conn == null) return;
        if (!conn.inUse.compareAndSet(true, false)) {
            throw new IllegalStateException("Double release detected or connection not in use");
        }

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

            // Overflow protection
            if (pool.size() >= maxSize) {
                conn.close();
                return;
            }

            if (shouldRebuild) {
                conn.close();
                conn = new Connection(); // rebuild
                // new connection inUse is false by default
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
        // Track usage state for double-release prevention
        final AtomicBoolean inUse = new AtomicBoolean(false);

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

