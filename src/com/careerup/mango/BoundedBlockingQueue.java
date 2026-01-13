package com.careerup.mango;

import java.util.*;
import java.util.concurrent.locks.*;

public class BoundedBlockingQueue<T> {

    private final Queue<T> queue = new ArrayDeque<>();
    private final int capacity;

    //How do you ensure fairness?
    //Use a fair lock:
    //new ReentrantLock(true)
    //Or use signalAll() (less efficient but fairer).
    private final Lock lock = new ReentrantLock();
    private final Condition notFull  = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    public BoundedBlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    // Producer: blocks when full
    public void put(T item) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == capacity) {
                notFull.await();     // wait until space available
            }
            queue.offer(item);
            notEmpty.signal();        // wake up one waiting consumer
        } finally {
            lock.unlock();
        }
    }

    // Consumer: blocks when empty
    public T take() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                notEmpty.await();     // wait until item available
            }
            T item = queue.poll();
            notFull.signal();         // wake up one waiting producer
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

