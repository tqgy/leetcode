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
     * Parse ISO 8601 timestamp to epoch seconds.
     * Format: "YYYY-MM-DDTHH:MM:SS"
     */
    private static long parseTimestamp(String isoTimestamp) {
        return java.time.Instant.parse(isoTimestamp + "Z").getEpochSecond();
    }

    /**
     * Count spam callers with fuzzy timestamp matching.
     * 
     * @param calls List of [caller, receiver, timestamp]
     * @param reports List of [receiver, timestamp]
     * @param toleranceSeconds Maximum time difference in seconds for matching (e.g., 2 for ±2 seconds)
     * @return List of [caller, spamCount] sorted by count desc, then caller asc
     */
    public static List<List<Object>> countSpamCallersWithTolerance(
            List<List<String>> calls, 
            List<List<String>> reports, 
            int toleranceSeconds) {

        // Parse all report timestamps and group by receiver
        // Map: receiver -> List of report timestamps (in epoch seconds)
        Map<String, List<Long>> spamReports = new HashMap<>();

        for (List<String> report : reports) {
            String receiver = report.get(0);
            long timestamp = parseTimestamp(report.get(1));
            
            spamReports.computeIfAbsent(receiver, k -> new ArrayList<>()).add(timestamp);
        }

        // Map: caller -> spam count
        Map<String, Integer> spamCount = new HashMap<>();

        for (List<String> call : calls) {
            String caller = call.get(0);
            String receiver = call.get(1);
            long callTime = parseTimestamp(call.get(2));

            // Ensure caller appears even if count is zero
            spamCount.putIfAbsent(caller, 0);

            // Check if this call matches any reports for the same receiver
            List<Long> reportTimes = spamReports.getOrDefault(receiver, Collections.emptyList());
            
            for (long reportTime : reportTimes) {
                // Match if within tolerance window
                if (Math.abs(callTime - reportTime) <= toleranceSeconds) {
                    spamCount.put(caller, spamCount.get(caller) + 1);
                }
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

        // Test fuzzy matching with tolerance
        System.out.println("\n=== Testing Fuzzy Timestamp Matching ===");
        testFuzzyMatching();
    }

    private static void testFuzzyMatching() {
        int failures = 0;

        Object[][] fuzzyTests = new Object[][]{
            // Test 1: Exact match with 2-second tolerance (should work like exact match)
            {
                List.of(List.of("A", "B", "2023-01-01T10:00:00")),
                List.of(List.of("B", "2023-01-01T10:00:00")),
                2,
                List.of(List.of("A", 1))
            },

            // Test 2: Match within +2 seconds
            {
                List.of(List.of("A", "B", "2023-01-01T10:00:00")),
                List.of(List.of("B", "2023-01-01T10:00:02")),
                2,
                List.of(List.of("A", 1))
            },

            // Test 3: Match within -2 seconds
            {
                List.of(List.of("A", "B", "2023-01-01T10:00:05")),
                List.of(List.of("B", "2023-01-01T10:00:03")),
                2,
                List.of(List.of("A", 1))
            },

            // Test 4: No match outside tolerance (+3 seconds)
            {
                List.of(List.of("A", "B", "2023-01-01T10:00:00")),
                List.of(List.of("B", "2023-01-01T10:00:03")),
                2,
                List.of(List.of("A", 0))
            },

            // Test 5: Multiple reports within tolerance window
            {
                List.of(List.of("A", "B", "2023-01-01T10:00:00")),
                List.of(
                    List.of("B", "2023-01-01T09:59:59"), // -1 sec
                    List.of("B", "2023-01-01T10:00:00"), // exact
                    List.of("B", "2023-01-01T10:00:01")  // +1 sec
                ),
                2,
                List.of(List.of("A", 3))
            },

            // Test 6: Multiple calls, some match, some don't
            {
                List.of(
                    List.of("A", "B", "2023-01-01T10:00:00"),
                    List.of("A", "B", "2023-01-01T10:00:10"),
                    List.of("C", "B", "2023-01-01T10:00:01")
                ),
                List.of(List.of("B", "2023-01-01T10:00:00")),
                2,
                List.of(List.of("A", 1), List.of("C", 1))
            },

            // Test 7: Different receivers, no cross-matching
            {
                List.of(
                    List.of("A", "B", "2023-01-01T10:00:00"),
                    List.of("C", "D", "2023-01-01T10:00:00")
                ),
                List.of(List.of("B", "2023-01-01T10:00:01")),
                2,
                List.of(List.of("A", 1), List.of("C", 0))
            },

            // Test 8: Boundary test - exactly at tolerance limit
            {
                List.of(List.of("A", "B", "2023-01-01T10:00:00")),
                List.of(List.of("B", "2023-01-01T10:00:02")),
                2,
                List.of(List.of("A", 1))
            },

            // Test 9: Boundary test - just outside tolerance
            {
                List.of(List.of("A", "B", "2023-01-01T10:00:00")),
                List.of(List.of("B", "2023-01-01T10:00:03")),
                2,
                List.of(List.of("A", 0))
            },

            // Test 10: Zero tolerance (exact match only)
            {
                List.of(
                    List.of("A", "B", "2023-01-01T10:00:00"),
                    List.of("C", "B", "2023-01-01T10:00:01")
                ),
                List.of(List.of("B", "2023-01-01T10:00:00")),
                0,
                List.of(List.of("A", 1), List.of("C", 0))
            }
        };

        int i = 0;
        for (Object[] t : fuzzyTests) {
            i++;
            @SuppressWarnings("unchecked")
            List<List<String>> calls = (List<List<String>>) t[0];
            @SuppressWarnings("unchecked")
            List<List<String>> reports = (List<List<String>>) t[1];
            int tolerance = (int) t[2];
            @SuppressWarnings("unchecked")
            List<List<Object>> expected = (List<List<Object>>) t[3];

            List<List<Object>> got = countSpamCallersWithTolerance(calls, reports, tolerance);

            if (compareResults(got, expected)) {
                System.out.printf("PASS fuzzy test %d (tolerance=%ds) -> %s%n", i, tolerance, got);
            } else {
                System.out.printf("FAIL fuzzy test %d (tolerance=%ds) -> got=%s expected=%s%n", i, tolerance, got, expected);
                failures++;
            }
        }

        if (failures > 0) {
            System.out.printf("\n%d fuzzy test(s) failed.%n", failures);
            System.exit(1);
        } else {
            System.out.println("\nAll fuzzy tests passed.");
        }
    }
}
