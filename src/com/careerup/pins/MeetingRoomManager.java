package com.careerup.pins;

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
    public static int mostUsedRoom(int n, int[][] meetings) {
        Arrays.sort(meetings, Comparator.comparingInt(a -> a[0]));

        // busy stores [endTime, roomId], sorted by endTime, then roomId
        PriorityQueue<int[]> busy = new PriorityQueue<>((a, b) ->
            a[0] != b[0] ? Integer.compare(a[0], b[0]) : Integer.compare(a[1], b[1])
        );
        PriorityQueue<Integer> free = new PriorityQueue<>();

        for (int i = 0; i < n; i++) 
            free.offer(i);

        int[] count = new int[n];

        for (int[] m : meetings) {
            int start = m[0], end = m[1];
            int duration = end - start;

            // Release rooms that have finished by the current meeting's start time
            while (!busy.isEmpty() && busy.peek()[0] <= start) {
                free.offer(busy.poll()[1]);
            }

            // !!!! If we could assure there is always a free room, we could skip this check
            // int room = free.poll();
            // busy.offer(new int[]{end, room});

            int assignedStartTime;
            int room;

            if (!free.isEmpty()) {
                // Room is available immediately
                room = free.poll();
                assignedStartTime = start;
            } else {
                // All rooms busy, wait for the earliest room to become free
                int[] earliest = busy.poll();
                assignedStartTime = earliest[0];
                room = earliest[1];
            }

            count[room]++;
            busy.offer(new int[]{assignedStartTime + duration, room});
        }

        int best = 0;
        for (int i = 1; i < n; i++) {
            if (count[i] > count[best])
                best = i;
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
        System.out.println("Most used room (rooms=2): " + mostUsedRoom(2, meetings)); // 0
        System.out.println("Max non-overlapping meetings: " + maxNonOverlappingMeetings(meetings)); // 2

        int[][] meetings2 = {
            {1, 2},
            {2, 3},
            {3, 4}
        };

        System.out.println("\nTest Set 2:");
        System.out.println("Can attend all: " + canAttendAll(meetings2)); // true
        System.out.println("Min rooms: " + minRooms(meetings2)); // 1
        System.out.println("Most used room (rooms=1): " + mostUsedRoom(1, meetings2)); // 0
        System.out.println("Max non-overlapping meetings: " + maxNonOverlappingMeetings(meetings2)); // 3

        // Case where meetings must wait
        int[][] meetings3 = {
            {0, 10},
            {1, 5},
            {2, 7},
            {3, 4}
        };
        System.out.println("\nTest Set 3 (Limited Rooms):");
        System.out.println("Most used room (rooms=2): " + mostUsedRoom(2, meetings3)); // 0 
    }
}
