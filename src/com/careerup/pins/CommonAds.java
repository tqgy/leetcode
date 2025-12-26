package com.careerup.pinterest;

import java.util.*;

/**
 * Problem Description
 * 
 * You receive a continuous stream of ad impression logs. Each log contains:
 * timestamp, adId, userId
 * 
 * You must support the following features:
 * 
 * Part 1: Global Top‑K Ads record(ts, ad, user) getTopK(k) 
 *  - Count total impressions for each ad across all time 
 *  - Return the top‑k ads by frequency 
 *  - Break ties by lexicographical order of adId
 * 
 * Part 2: Sliding Window Top‑K Ads getTopK(k, windowSize, currentTime) 
 *  - Only count impressions within the last windowSize seconds 
 *  - Automatically remove expired logs 
 *  - Return top‑k ads by frequency in the window
 * 
 * Follow‑up 1: Top‑K Ads by Unique Users getTopKByUniqueUsers(k) 
 *  - Count distinct users per ad (global) 
 *  - Return top‑k ads by unique user count
 * 
 * Follow‑up 2: Top‑K Campaigns Assume adId format is "campaignId_adId" getTopCampaigns(k) 
 *  - Aggregate impressions by campaign 
 *  - Return top‑k campaigns by total impressions
 * 
 * Follow‑up 3: Sliding Window Unique Users getTopKUniqueUsers(k, windowSize, currentTime) 
 *  - Count distinct users per ad within the sliding window 
 *  - Return top‑k ads by unique user count
 */

public class CommonAds {

    private static class Log {
        int ts;
        String ad;
        String user;

        Log(int ts, String ad, String user) {
            this.ts = ts;
            this.ad = ad;
            this.user = user;
        }
    }

    // Part 1: global counts
    private Map<String, Integer> globalFreq = new HashMap<>();
    private Map<String, Set<String>> globalUsers = new HashMap<>();

    // Part 2: sliding window
    private Deque<Log> window = new ArrayDeque<>();
    private Map<String, Integer> windowFreq = new HashMap<>();
    private Map<String, Set<String>> windowUsers = new HashMap<>();

    // Record a new log entry
    public void record(int timestamp, String adId, String userId) {
        // Global frequency
        globalFreq.put(adId, globalFreq.getOrDefault(adId, 0) + 1);
        globalUsers.computeIfAbsent(adId, k -> new HashSet<>()).add(userId);

        // Sliding window structures
        window.addLast(new Log(timestamp, adId, userId));
        windowFreq.put(adId, windowFreq.getOrDefault(adId, 0) + 1);
        windowUsers.computeIfAbsent(adId, k -> new HashSet<>()).add(userId);
    }

    // Helper: build top‑k from a frequency map
    private List<String> topKFromFreq(Map<String, Integer> freq, int k) {
        PriorityQueue<String> pq = new PriorityQueue<>((a, b) -> {
            int fa = freq.get(a);
            int fb = freq.get(b);
            if (fa != fb)
                return fa - fb; // min‑heap by frequency
            return a.compareTo(b); 
        });

        for (String ad : freq.keySet()) {
            pq.offer(ad);
            if (pq.size() > k)
                pq.poll();
        }

        List<String> res = new ArrayList<>();
        while (!pq.isEmpty())
            res.add(pq.poll());
        return res;
    }

    // Part 1: global top‑k ads
    public List<String> getTopK(int k) {
        return topKFromFreq(globalFreq, k);
    }

    // Remove expired logs from sliding window
    private void cleanWindow(int windowSize, int currentTime) {
        while (!window.isEmpty() && window.peekFirst().ts < currentTime - windowSize) {
            Log old = window.pollFirst();

            // Decrease frequency
            windowFreq.put(old.ad, windowFreq.get(old.ad) - 1);
            if (windowFreq.get(old.ad) == 0)
                windowFreq.remove(old.ad);

            // Remove user
            Set<String> users = windowUsers.get(old.ad);
            users.remove(old.user);
            if (users.isEmpty())
                windowUsers.remove(old.ad);
        }
    }

    // Part 2: sliding window top‑k ads
    public List<String> getTopK(int k, int windowSize, int currentTime) {
        cleanWindow(windowSize, currentTime);
        return topKFromFreq(windowFreq, k);
    }

    // Follow‑up 1: top‑k ads by unique users (global)
    public List<String> getTopKByUniqueUsers(int k) {
        Map<String, Integer> freq = new HashMap<>();
        for (String ad : globalUsers.keySet()) {
            freq.put(ad, globalUsers.get(ad).size());
        }
        return topKFromFreq(freq, k);
    }

    // Follow‑up 2: top‑k campaigns
    public List<String> getTopCampaigns(int k) {
        Map<String, Integer> campaignFreq = new HashMap<>();

        for (String ad : globalFreq.keySet()) {
            String campaign = ad.split("_")[0];
            campaignFreq.put(campaign, campaignFreq.getOrDefault(campaign, 0) + globalFreq.get(ad));
        }

        return topKFromFreq(campaignFreq, k);
    }

    // Follow‑up 3: sliding window unique users
    public List<String> getTopKUniqueUsers(int k, int windowSize, int currentTime) {
        cleanWindow(windowSize, currentTime);

        Map<String, Integer> freq = new HashMap<>();
        for (String ad : windowUsers.keySet()) {
            freq.put(ad, windowUsers.get(ad).size());
        }

        return topKFromFreq(freq, k);
    }

    // Test suite
    public static void main(String[] args) {
        CommonAds ads = new CommonAds();

        ads.record(1000, "C1_A", "U1");
        ads.record(1001, "C1_B", "U2");
        ads.record(1002, "C1_A", "U3");
        ads.record(1005, "C2_C", "U1");
        ads.record(1006, "C1_A", "U1");
        ads.record(1007, "C2_C", "U2");

        System.out.println("=== Part 1: Global Top2 ===");
        System.out.println(ads.getTopK(2)); // Expected: [C1_A, C2_C]

        System.out.println("=== Part 2: Sliding Window Top2 (window=5, now=1007) ===");
        System.out.println(ads.getTopK(2, 5, 1007)); // Expected: [C1_A, C2_C]

        System.out.println("=== Follow-up 1: Top2 by Unique Users ===");
        System.out.println(ads.getTopKByUniqueUsers(2)); // Expected: [C1_A, C2_C]

        System.out.println("=== Follow-up 2: Top Campaigns ===");
        System.out.println(ads.getTopCampaigns(2)); // Expected: [C1, C2]

        System.out.println("=== Follow-up 3: Sliding Window Unique Users ===");
        System.out.println(ads.getTopKUniqueUsers(2, 5, 1007)); // Expected: [C1_A, C2_C]
    }
}
