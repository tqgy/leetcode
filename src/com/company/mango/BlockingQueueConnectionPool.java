package com.careerup.mango;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class BlockingQueueConnectionPool {

    private final BlockingQueue<Connection> pool;
    private final int maxSize;
    private volatile boolean closed = false;

    public BlockingQueueConnectionPool(int maxSize) {
        this.maxSize = maxSize;
        this.pool = new ArrayBlockingQueue<>(maxSize);
        for (int i = 0; i < maxSize; i++) {
            pool.offer(new Connection()); // lazy or eager, here we just create them
        }
    }

    // Acquire a connection (blocks if none available)
    public Connection acquire() throws InterruptedException {
        if (closed) {
            throw new IllegalStateException("Pool is closed");
        }

        // take() blocks until an element is available
        Connection conn = pool.take();

        if (closed) {
            // If closed while waiting or immediately after, try to return it or just ignore
            // In strict sense, if closed, we might want to throw.
            // Reuse return path to ensure we don't leak if we want to be clean,
            // but effectively we can just throw.
            // For now, simple check.
            conn.close();
            throw new IllegalStateException("Pool is closed");
        }

        // Mark as in-use
        conn.inUse.set(true);
        conn.open(); // open outside any lock (BlockingQueue handles internal locks)
        return conn;
    }

    // Acquire with timeout
    public Connection acquire(long timeout, TimeUnit unit) throws InterruptedException {
        if (closed) {
            throw new IllegalStateException("Pool is closed");
        }

        Connection conn = pool.poll(timeout, unit);
        if (conn == null) {
            return null; // timed out
        }

        if (closed) {
            conn.close();
            throw new IllegalStateException("Pool is closed");
        }

        conn.inUse.set(true);
        conn.open();
        return conn;
    }

    // Release a connection back to pool
    public void release(Connection conn, boolean shouldRebuild) {
        if (conn == null)
            return;

        // Double release check
        if (!conn.inUse.compareAndSet(true, false)) {
            throw new IllegalStateException("Double release detected or connection not in use");
        }

        if (closed) {
            conn.close();
            return;
        }

        if (shouldRebuild) {
            conn.close();
            conn = new Connection();
        }

        // offer returns false if queue is full.
        // Since we have a fixed set of connections that circulate,
        // this should only happen if we somehow created more connections than maxSize.
        // We can check return value to be safe.
        boolean added = pool.offer(conn);
        if (!added) {
            // Should theoretically not happen if logic is correct and no one else inserts
            // into queue
            conn.close();
        }
    }

    // Close entire pool
    public void close() {
        closed = true;
        BlockingQueue<Connection> tempPool = new ArrayBlockingQueue<>(maxSize);
        // *Atomicity*: 
        // drainTo atomically removes all available elements from the BlockingQueue and 
        // adds them to the collection you provide. This happens as a single unit of work.
        //
        // *Preventing access*: 
        // By "draining" the queue, we immediately ensure no other thread can acquire()
        // these connections. If we just iterated over the queue (e.g., for (Connection
        // c : pool)), other threads could still potentially grab those connections
        // while we are trying to close them, leading to race conditions.
        //
        // *Ownership*: 
        // It effectively transfers ownership of all idle connections to the current thread, allowing us to safely call
        // close() on each one without worrying about concurrency.
        pool.drainTo(tempPool);
        for (Connection conn : tempPool) {
            conn.close();
        }
    }

    // Reuse the same Connection class structure
    public static class Connection {
        private boolean opened = false;
        final AtomicBoolean inUse = new AtomicBoolean(false);

        public void open() {
            if (!opened) {
                opened = true;
            }
        }

        public void runCommand(String cmd) {
            if (!opened)
                throw new IllegalStateException("Not opened");
        }

        public void close() {
            if (opened) {
                opened = false;
            }
        }
    }
}
