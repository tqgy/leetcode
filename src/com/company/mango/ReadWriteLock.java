package com.careerup.mango;

import java.util.concurrent.locks.*;

/**
 * State: - activeReaders ≥ 0 - activeWriters ∈ {0,1} - waitingWriters ≥ 0
 * 
 * Invariants: - If activeWriters == 1 → activeReaders == 0 - Multiple readers
 * allowed when no writer is active or waiting (depending on policy).
 * 
 * Writer preference: - Readers block if waitingWriters > 0, avoiding writer
 * starvation.
 * 
 * Concurrency primitive: - Single ReentrantLock protects all shared state; no
 * volatile needed.
 */
public class ReadWriteLock {

    private final ReentrantLock lock = new ReentrantLock(true); // fair
    private final Condition canRead = lock.newCondition();
    private final Condition canWrite = lock.newCondition();

    private int activeReaders = 0;
    private int activeWriters = 0; // 0 or 1
    private int waitingWriters = 0; // for writer preference

    public void lockRead() throws InterruptedException {
        lock.lock();
        try {
            // Writer Preference:
            // If there are any active writers OR waiting writers, we block the reader.
            // This prevents "starvation" of writers by a constant stream of readers.
            while (activeWriters > 0 || waitingWriters > 0) {
                canRead.await();
            }
            activeReaders++;
        } finally {
            lock.unlock();
        }
    }

    public void unlockRead() {
        lock.lock();
        try {
            activeReaders--;
            // If this was the last reader, it's safe to check if a writer is waiting
            if (activeReaders == 0) {
                canWrite.signal(); // Wake up one writer (if any)
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Uses nested try blocks to ensure the waitingWriters count is always decremented,
     * even if the thread is interrupted while waiting.
     * 
     * Here is the breakdown:
     * 
     * 1. Outer try: 
     * - Increments waitingWriters++ (signaling intent to write). 
     * - Enters the inner try. 
     * 
     * 2. Inner try ... finally: 
     * - Logic: Waits (await()) until it's safe to write. 
     * - Crucial Safety: If await() throws InterruptedException, the finally block
     * guarantees waitingWriters-- runs. 
     *  - Without this, an interrupted thread would leave waitingWriters permanently
     * incremented, causing readers to be blocked forever (due to the writer
     * preference logic waitingWriters > 0).
     * 
     * 3. Outer finally: 
     * - Unlocks the main lock .
     * 
     * @throws InterruptedException
     */
    public void lockWrite() throws InterruptedException {
        lock.lock();
        try {
            // Signal that we want to write (helps prevent new readers from entering)
            waitingWriters++;
            try {
                // We must wait if ANYONE is reading or writing
                while (activeReaders > 0 || activeWriters > 0) {
                    canWrite.await();
                }
            } finally {
                // We stopped waiting (either we got the lock or were interrupted)
                waitingWriters--;
            }
            activeWriters = 1;
        } finally {
            lock.unlock();
        }
    }

    public void unlockWrite() {
        lock.lock();
        try {
            activeWriters = 0;
            // Signal one waiting writer to go next (if any)
            // If no writers, checking signalAll() for readers might be handled by logic or
            // explicit signal
            canWrite.signal();

            // Also wake up all readers.
            // The logic in lockRead/lockWrite + Fair ReentrantLock handles who actually
            // gets it.
            // But typically if we just finished writing, we let everyone check.
            canRead.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
