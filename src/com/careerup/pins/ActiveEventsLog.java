package com.careerup.pins;

import java.util.*;

/**
 * ActiveEventsLog - Processes event logs and generates a timeline showing how
 * many events are active at each minute.
 * 
 * <p>
 * Input Format: Each event is represented as a String array [startTime, endTime, eventName]
 * where time values are numeric strings representing minutes.
 * 
 * <p>
 * Important Semantic Detail: The interval is [start, end) - meaning the event
 * is active at: start, start+1, start+2, ..., end-1. NOT active at end.
 * 
 * <p>
 * Example: [10, 13, "A"] means the event is active at minutes 10, 11, 12 (but not 13).
 * 
 * <p>
 * Output Format: A list of [time, n_active_events] pairs showing how many
 * events are active at each minute within the time range of all events.
 * 
 * <p>
 * Algorithm: Uses a sweep line approach with a difference array:
 * - At start time: increment active count (+1)
 * - At end time: decrement active count (-1)
 * - Sweep through all time points in order and generate the output log
 */
public class ActiveEventsLog {

    /**
     * Builds an active events log from a list of events.
     * 
     * <p>
     * Algorithm Overview:
     * 1. Use a TreeMap to track changes in active event count at each time point
     * 2. For each event [start, end):
     *    - Increment count at start time
     *    - Decrement count at end time
     * 3. Sweep through all time points in order, maintaining current active count
     * 4. Generate output entries for each minute showing the active count
     * 
     * @param events List of String arrays, each representing [start, end, name]
     *               where start and end are numeric strings representing minutes
     * @return List of [time, n_active_events] pairs, one per minute
     */
    public static List<int[]> buildActiveLog(List<String[]> events) {
        // TreeMap tracks delta changes: key = time point, value = change in active count
        // +1 when an event starts, -1 when an event ends
        TreeMap<Integer, Integer> diff = new TreeMap<>();

        // First pass: build the difference map by processing all events
        for (String[] event : events) {
            // Parse times from String array: [start, end, name]
            int start = Integer.parseInt(event[0]);
            int end = Integer.parseInt(event[1]); // [start, end) semantics - end is exclusive

            // Event starts: increment active count at start time
            diff.put(start, diff.getOrDefault(start, 0) + 1);

            // Event ends: decrement active count at end time
            // (end is exclusive, so event stops being active at this time)
            diff.put(end, diff.getOrDefault(end, 0) - 1);
        }

        List<int[]> result = new ArrayList<>();
        int curActive = 0; // Current number of active events
        int lastTime = diff.firstKey(); // Start from the earliest time point

        // Second pass: sweep through change points chronologically and generate output
        for (var entry : diff.entrySet()) {
            int time = entry.getKey();
            int delta = entry.getValue();

            // For each minute in [lastTime, time), the active count is curActive
            // Generate output entries for all minutes in this interval
            for (int t = lastTime; t < time; t++) {
                result.add(new int[] { t, curActive });
            }

            // Apply the delta change and move to the next time point
            curActive += delta;
            lastTime = time;
        }

        return result;
    }

    /**
     * Example usage demonstrating the active events log generation.
     * 
     * <p>
     * Example events:
     * - Event A: [1, 4) - active at minutes 1, 2, 3
     * - Event B: [2, 5) - active at minutes 2, 3, 4
     * - Event C: [3, 6) - active at minutes 3, 4, 5
     * - Event D: [4, 7) - active at minutes 4, 5, 6
     * - Event G: [7, 10) - active at minutes 7, 8, 9
     * - Event H: [8, 11) - active at minutes 8, 9, 10
     * - Event I: [9, 12) - active at minutes 9, 10, 11
     * 
     * <p>
     * Expected output (first few minutes):
     * - Minute 1: 1 event (A)
     * - Minute 2: 2 events (A, B)
     * - Minute 3: 3 events (A, B, C)
     * - Minute 4: 3 events (B, C, D)
     * - Minute 5: 2 events (C, D)
     */
    public static void main(String[] args) {
        List<String[]> events = Arrays.asList(new String[] { "1", "4", "A" }, // active at 1, 2, 3
                new String[] { "2", "5", "B" }, // active at 2, 3, 4
                new String[] { "3", "6", "C" }, // active at 3, 4, 5
                new String[] { "4", "7", "D" }, // active at 4, 5, 6
                new String[] { "7", "10", "G" }, // active at 7, 8, 9
                new String[] { "8", "11", "H" }, // active at 8, 9, 10
                new String[] { "9", "12", "I" } // active at 9, 10, 11
        );

        List<int[]> log = buildActiveLog(events);
        for (int[] row : log) {
            System.out.println("time=" + row[0] + ", n_events=" + row[1]);
        }
    }
}
