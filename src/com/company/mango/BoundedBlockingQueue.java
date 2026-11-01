package com.careerup.mango;

import java.util.*;
import java.util.concurrent.locks.*;

public class BoundedBlockingQueue<T> {

    private final Queue<T> queue = new ArrayDeque<>();
    private final int capacity;

    // Use fairness to prevent thread starvation if needed.
    // ReentrantLock(true) grants access to the longest-waiting thread.
    private final Lock lock;
    private final Condition notFull;
    private final Condition notEmpty;

    /**
     * Creates a BoundedBlockingQueue with the given capacity and default (non-fair) access policy.
     *
     * @param capacity the maximum number of elements the queue can hold
     * @throws IllegalArgumentException if capacity is less than 1
     */
    public BoundedBlockingQueue(int capacity) {
        this(capacity, false);
    }

    /**
     * Creates a BoundedBlockingQueue with the given capacity and the specified access policy.
     *
     * @param capacity the maximum number of elements the queue can hold
     * @param fair if true, then queue accesses for threads blocked on insertion or removal,
     *             are processed in FIFO order; if false, the access order is unspecified.
     * @throws IllegalArgumentException if capacity is less than 1
     */
    public BoundedBlockingQueue(int capacity, boolean fair) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
        this.lock = new ReentrantLock(fair);
        this.notFull = lock.newCondition();
        this.notEmpty = lock.newCondition();
    }

    /**
     * Inserts the specified element into this queue, waiting if necessary
     * for space to become available.
     *
     * @param item the element to add
     * @throws InterruptedException if interrupted while waiting
     * @throws NullPointerException if the specified item is null
     */
    public void put(T item) throws InterruptedException {
        if (item == null) throw new NullPointerException("Item cannot be null");
        lock.lock();
        try {
            while (queue.size() == capacity) {
                // Wait until space is available.
                // Releasing lock allows consumers to take items.
                notFull.await(); 
            }
            queue.offer(item);
            
            // Signal a single waiting consumer that data is available.
            // Using signal() instead of signalAll() is more efficient (avoids thundering herd)
            // provided that threads are uniform (all waiting for the same condition).
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Retrieves and removes the head of this queue, waiting if necessary
     * until an element becomes available.
     *
     * @return the head of this queue
     * @throws InterruptedException if interrupted while waiting
     */
    public T take() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                // Wait until an item is available.
                // Releasing lock allows producers to put items.
                notEmpty.await();
            }
            T item = queue.poll();
            
            // Signal a single waiting producer that space is available.
            notFull.signal();
            return item;
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }
}

