package com.careerup.pins;

import java.util.*;

class MedianFinder {
    PriorityQueue<Integer> maxHeap; // lower half
    PriorityQueue<Integer> minHeap; // upper half

    public MedianFinder() {
        maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        minHeap = new PriorityQueue<>();
    }

    public void addNum(int num) {
        maxHeap.add(num);
        minHeap.add(maxHeap.poll());

        if (minHeap.size() > maxHeap.size()) {
            maxHeap.add(minHeap.poll());
        }
    }

    public double findMedian() {
        if (maxHeap.size() > minHeap.size()) {
            return maxHeap.peek();
        }
        return (maxHeap.peek() + minHeap.peek()) / 2.0;
    }

    public static void main(String[] args) {
        MedianFinder finder = new MedianFinder();
        finder.addNum(1);
        finder.addNum(2);
        System.out.println(finder.findMedian()); // 1.5
        finder.addNum(3);
        System.out.println(finder.findMedian()); // 2.0
        finder.addNum(4);
        System.out.println(finder.findMedian()); // 2.5 
        finder.addNum(5);
        System.out.println(finder.findMedian()); // 3.0
    }
}
