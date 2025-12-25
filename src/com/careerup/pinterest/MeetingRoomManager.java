package com.careerup.pinterest;

import java.util.*;

/**
 * MeetingRoomManager - solutions to meeting room scheduling problems:
 * 1. Can a person attend all meetings?
 * 2. Minimum number of meeting rooms required.
 * 3. Which meeting room is used the most?
 * 4. Max non-overlapping meetings that can be attended.
 */
public class MeetingRoomManager {

    // 1. Meeting Rooms I — check if a person can attend all meetings
    public static boolean canAttendAll(int[][] meetings) {
        Arrays.sort(meetings, Comparator.comparingInt(a -> a[0]));
        for (int i = 1; i < meetings.length; i++) {
            if (meetings[i][0] < meetings[i - 1][1]) {
                return false;
            }
        }
        return true;
    }

    // 2. Meeting Rooms II — minimum number of rooms required
    public static int minRooms(int[][] meetings) {
        Arrays.sort(meetings, Comparator.comparingInt(a -> a[0]));

        PriorityQueue<Integer> pq = new PriorityQueue<>(); // stores end times

        for (int[] m : meetings) {
            if (!pq.isEmpty() && pq.peek() <= m[0]) {
                pq.poll(); // reuse room
            }
            pq.offer(m[1]);
        }

        return pq.size();
    }

    // 3. Meeting Rooms III — find the room used the most
    public static int mostUsedRoom(int[][] meetings) {
        Arrays.sort(meetings, Comparator.comparingInt(a -> a[0]));

        int n = meetings.length;
        PriorityQueue<int[]> busy = new PriorityQueue<>(Comparator.comparingInt(a -> a[0])); 
        PriorityQueue<Integer> free = new PriorityQueue<>();

        for (int i = 0; i < n; i++) 
            free.offer(i);

        int[] count = new int[n];

        for (int[] m : meetings) {
            int start = m[0], end = m[1];
            while (!busy.isEmpty() && busy.peek()[0] <= start) {
                free.offer(busy.poll()[1]);
            }
            int room = free.poll();
            count[room]++;
            busy.offer(new int[]{end, room});
        }

        int best = 0;
        for (int i = 1; i < n; i++) {
            if (count[i] > count[best]) best = i;
        }
        return best;
    }

    // 4. Meeting Rooms IV — maximum number of non-overlapping meetings
    public static int maxNonOverlappingMeetings(int[][] meetings) {
        Arrays.sort(meetings, Comparator.comparingInt(a -> a[1]));
        int count = 0;
        int lastEnd = Integer.MIN_VALUE;
        for (int[] m : meetings) {
            if (m[0] >= lastEnd) {
                count++;
                lastEnd = m[1];
            }
        }
        return count;
    }

    // Simple test runner
    public static void main(String[] args) {
        int[][] meetings = {
            {0, 30},
            {5, 10},
            {15, 20}
        };

        System.out.println("Can attend all: " + canAttendAll(meetings)); // false
        System.out.println("Min rooms: " + minRooms(meetings)); // 2
        System.out.println("Most used room: " + mostUsedRoom(meetings)); // 0 or 1 depending on allocation
        System.out.println("Max non-overlapping meetings: " + maxNonOverlappingMeetings(meetings)); // 2

        int[][] meetings2 = {
            {1, 2},
            {2, 3},
            {3, 4}
        };

        System.out.println("Can attend all: " + canAttendAll(meetings2)); // true
        System.out.println("Min rooms: " + minRooms(meetings2)); // 1
        System.out.println("Most used room: " + mostUsedRoom(meetings2)); // 0
        System.out.println("Max non-overlapping meetings: " + maxNonOverlappingMeetings(meetings2)); // 3
    }
}
