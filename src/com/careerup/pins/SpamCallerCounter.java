package com.careerup.pins;

/*
 * SpamCallerCounter
 *
 * Inputs:
 *  - calls:   list of [caller, receiver, timestamp] (ISO 8601: "YYYY-MM-DDTHH:MM:SS")
 *  - reports: list of [receiver, timestamp]
 *
 * Matching rule (exact): a call is linked to a report if
 *   call.receiver.equals(report.receiver) && call.timestamp.equals(report.timestamp)
 *
 * Details:
 *  - Each matching call counts (multiple calls at same timestamp each count).
 *  - Multiple reports at same timestamp also count separately.
 *  - Callers with zero matches must still appear with count 0.
 *
 * Output: list of pairs [caller, spamCount], sorted by
 *  1) descending spamCount, then
 *  2) ascending caller (lexicographically) for ties.
 */
import java.util.*;

public class SpamCallerCounter {

    public static List<List<Object>> countSpamCallers(List<List<String>> calls, List<List<String>> reports) {

        // Map: receiver -> (timestamp -> reportCount). Use counts because
        // multiple reports at the same timestamp should count separately.
        Map<String, Map<String, Integer>> spamReports = new HashMap<>();

        for (List<String> report : reports) {
            String receiver = report.get(0);
            String timestamp = report.get(1);

            Map<String, Integer> receiverMap = spamReports.computeIfAbsent(receiver, k -> new HashMap<>());
            int prev = receiverMap.getOrDefault(timestamp, 0);
            receiverMap.put(timestamp, prev + 1);
        }

        // Map: caller -> spam count (may include zero-count callers)
        Map<String, Integer> spamCount = new HashMap<>();

        for (List<String> call : calls) {
            String caller = call.get(0);
            String receiver = call.get(1);
            String timestamp = call.get(2);

            // Ensure caller appears even if count is zero
            spamCount.putIfAbsent(caller, 0);

            int reportsForTimestamp = spamReports.getOrDefault(receiver, Collections.emptyMap())
                    .getOrDefault(timestamp, 0);

            if (reportsForTimestamp > 0) {
                // Each matching report counts separately. If multiple calls exist
                // at the same timestamp they are handled independently in the loop.
                spamCount.put(caller, spamCount.get(caller) + reportsForTimestamp);
            }
        }

        // Convert to output format
        List<List<Object>> result = new ArrayList<>();
        for (var entry : spamCount.entrySet()) {
            result.add(List.of(entry.getKey(), entry.getValue()));
        }

        // Sort: spamCount desc, caller asc
        result.sort((a, b) -> {
            int countA = (int) a.get(1);
            int countB = (int) b.get(1);

            if (countA != countB) {
                return Integer.compare(countB, countA); // descending
            }
            return ((String) a.get(0)).compareTo((String) b.get(0)); // ascending
        });

        return result;
    }

    /**
     * Compare two result lists for exact equality.
     * Accepts numeric values as any Number and compares by int value.
     * Expects each entry to be a pair: [caller (String), count (Number)].
     */
    private static boolean compareResults(List<List<Object>> got, List<List<Object>> expected) {
        if (got == null && expected == null) return true;
        if (got == null || expected == null) return false;
        if (got.size() != expected.size()) return false;

        for (int i = 0; i < got.size(); ++i) {
            List<Object> g = got.get(i);
            List<Object> e = expected.get(i);

            if (g.size() < 2 || e.size() < 2) return false;

            String gName = String.valueOf(g.get(0));
            String eName = String.valueOf(e.get(0));
            if (!gName.equals(eName)) return false;

            Object gv = g.get(1);
            Object ev = e.get(1);
            if (!(gv instanceof Number) || !(ev instanceof Number)) {
                if (!Objects.equals(gv, ev)) return false;
            } else {
                if (((Number) gv).intValue() != ((Number) ev).intValue()) return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        int failures = 0;

        Object[][] tests = new Object[][]{
            // Simple matching
            {
                List.of(List.of("A", "B", "2023-01-01T10:00:00"), List.of("C", "B", "2023-01-01T10:00:00"), List.of("A", "D", "2023-01-01T11:00:00")),
                List.of(List.of("B", "2023-01-01T10:00:00")),
                List.of(List.of("A", 1), List.of("C", 1))
            },

            // Duplicate reports and duplicate calls -> counts multiply
            {
                List.of(List.of("A", "B", "2023-01-01T10:00:00"), List.of("A", "B", "2023-01-01T10:00:00")),
                List.of(List.of("B", "2023-01-01T10:00:00"), List.of("B", "2023-01-01T10:00:00")),
                List.of(List.of("A", 4))
            },

            // Callers with zero counts must appear and sorting order check
            {
                List.of(List.of("A", "B", "2023-01-01T10:00:00"), List.of("B", "C", "2023-01-01T11:00:00"), List.of("D", "E", "2023-01-01T12:00:00")),
                List.of(List.of("C", "2023-01-01T11:00:00")),
                List.of(List.of("B", 1), List.of("A", 0), List.of("D", 0))
            },

            // No reports at all
            {
                List.of(List.of("X", "Y", "2023-01-01T09:00:00"), List.of("A", "B", "2023-01-01T09:05:00")),
                List.of(),
                List.of(List.of("A", 0), List.of("X", 0))
            }
        };

        int i = 0;
        for (Object[] t : tests) {
            i++;
            @SuppressWarnings("unchecked")
            List<List<String>> calls = (List<List<String>>) t[0];
            @SuppressWarnings("unchecked")
            List<List<String>> reports = (List<List<String>>) t[1];
            @SuppressWarnings("unchecked")
            List<List<Object>> expected = (List<List<Object>>) t[2];

            List<List<Object>> got = countSpamCallers(calls, reports);

            if (compareResults(got, expected)) {
                System.out.printf("PASS test %d -> %s%n", i, got);
            } else {
                System.out.printf("FAIL test %d -> got=%s expected=%s%n", i, got, expected);
                failures++;
            }
        }

        if (failures > 0) {
            System.out.printf("\n%d test(s) failed.%n", failures);
            System.exit(1);
        } else {
            System.out.println("\nAll tests passed.");
        }
    }
}
