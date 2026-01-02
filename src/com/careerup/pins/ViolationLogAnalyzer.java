package com.careerup.pins;

import java.util.*;

/**
 * -----------------------------------------------------------------------------
 *  Violation Log Analyzer — Full Problem Description
 * -----------------------------------------------------------------------------
 *
 * A company uses machine learning to detect whether user posts violate content
 * policies. Each violation is recorded as a log entry. You are given a list of
 * logs sorted by date (ascending).
 *
 * Each log contains:
 *   - postId : A unique identifier for the post (e.g., "0", "p123")
 *   - policy : The type of violation (e.g., "spam", "privacy", "child")
 *   - date   : The date of the violation in "YYYY-MM-DD" format
 *
 * You must design a ViolationLogAnalyzer that supports the following queries:
 *
 *   1. countUniquePosts(startDate, endDate)
 *        → Return the number of UNIQUE postIds that have at least one violation
 *          between startDate and endDate (inclusive).
 *
 *   2. countUniquePolicies(startDate, endDate)
 *        → Return the number of UNIQUE policies that appear in violations
 *          between startDate and endDate (inclusive).
 *
 *   3. countViolationsByDate(date)
 *        → Return the total number of violations that occurred on that date.
 *
 * Constraints:
 *   - Logs are already sorted by date.
 *   - Up to ~100k logs and ~100k queries.
 *   - Queries must be efficient (binary search + scanning).
 *
 * Approach:
 *   - Extract all dates into a list for binary search.
 *   - Precompute a map: date → count of violations.
 *   - For range queries, use binary search to find the log slice, then scan
 *     that slice to collect unique postIds or policies.
 *
 * Time Complexity:
 *   - Binary search: O(log n)
 *   - Range scan: O(k) where k is number of logs in the date range
 * -----------------------------------------------------------------------------
 */
public class ViolationLogAnalyzer {

    /**
     * Represents a single violation log entry.
     */
    static class Log {
        String postId;
        String policy;
        String date;

        Log(String postId, String policy, String date) {
            this.postId = postId;
            this.policy = policy;
            this.date = date;
        }
    }

    // The original list of logs (already sorted by date)
    private final List<Log> logs;

    // A list of dates extracted from logs, used for binary search
    private final List<String> dates;

    // Precomputed map: date → number of violations on that date
    private final Map<String, Integer> dateCountMap = new HashMap<>();


    /**
     * Constructor: initializes the analyzer with sorted logs.
     * Precomputes:
     *   - A list of dates for binary search
     *   - A map of date → violation count
     */
    public ViolationLogAnalyzer(List<Log> logs) {
        this.logs = logs;

        dates = new ArrayList<>();
        for (Log log : logs) {
            dates.add(log.date);
            dateCountMap.put(log.date, dateCountMap.getOrDefault(log.date, 0) + 1);
        }
    }


    /**
     * Binary search: find the first index where date >= target.
     * Equivalent to lower_bound in C++.
     */
    private int lowerBound(String target) {
        int left = 0, right = dates.size();
        while (left < right) {
            int mid = (left + right) / 2;
            if (dates.get(mid).compareTo(target) >= 0) {
                right = mid; // mid might be the first valid index
            } else {
                left = mid + 1;
            }
        }
        return left;
    }


    /**
     * Binary search: find the first index where date > target.
     * Equivalent to upper_bound in C++.
     */
    private int upperBound(String target) {
        int left = 0, right = dates.size();
        while (left < right) {
            int mid = (left + right) / 2;
            if (dates.get(mid).compareTo(target) > 0) {
                right = mid; // mid is too large or just right
            } else {
                left = mid + 1;
            }
        }
        return left;
    }


    /**
     * Query 1:
     * Count UNIQUE postIds in the date range [startDate, endDate].
     */
    public int countUniquePosts(String startDate, String endDate) {
        int start = lowerBound(startDate);
        int end = upperBound(endDate);

        Set<String> unique = new HashSet<>();
        for (int i = start; i < end; i++) {
            unique.add(logs.get(i).postId);
        }
        return unique.size();
    }


    /**
     * Query 2:
     * Count UNIQUE policies in the date range [startDate, endDate].
     */
    public int countUniquePolicies(String startDate, String endDate) {
        int start = lowerBound(startDate);
        int end = upperBound(endDate);

        Set<String> unique = new HashSet<>();
        for (int i = start; i < end; i++) {
            unique.add(logs.get(i).policy);
        }
        return unique.size();
    }


    /**
     * Query 3:
     * Count total violations on a specific date.
     * Uses precomputed map for O(1) lookup.
     */
    public int countViolationsByDate(String date) {
        return dateCountMap.getOrDefault(date, 0);
    }


    /**
     * Example usage.
     */
    public static void main(String[] args) {
        List<Log> logs = Arrays.asList(
            new Log("p1", "spam", "2024-01-01"),
            new Log("p2", "privacy", "2024-01-02"),
            new Log("p1", "spam", "2024-01-03"),
            new Log("p3", "child", "2024-01-03")
        );

        ViolationLogAnalyzer analyzer = new ViolationLogAnalyzer(logs);

        System.out.println(analyzer.countUniquePosts("2024-01-01", "2024-01-03"));     // 3
        System.out.println(analyzer.countUniquePolicies("2024-01-01", "2024-01-03"));  // 3
        System.out.println(analyzer.countViolationsByDate("2024-01-03"));              // 2
    }
}

