package com.careerup.pins;

/*
 * ShortestHikingDistance
 *
 * Given non-decreasing stops `t` (t[0] == 0) and up to `k` rest stops, choose
 * stops so the maximum daily travel distance is minimized.
 *
 * Implementation notes:
 * - Binary search the answer D (minimum possible maximum daily distance).
 * - Greedy `canHike` checks whether it's possible with at most k rests by
 *   walking forward and placing rests only when forced.
 */

public class ShortestHikingDistance {

    public static int minimizeMaxDailyDistance(int[] t, int k) {
        int n = t.length;

        int left = 0; // minimum possible max distance
        int right = t[n - 1] - t[0]; // maximum possible max distance
        int answer = right;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (canHike(t, k, mid)) {
                answer = mid;
                right = mid - 1; // try smaller max distance
            } else {
                left = mid + 1; // need larger max distance
            }
        }

        return answer;
    }

    // Check if we can complete the hike with max daily distance <= D
    private static boolean canHike(int[] t, int k, int D) {
        int restsUsed = 0;
        int lastStop = t[0];

        for (int i = 1; i < t.length; i++) {
            if (t[i] - lastStop > D) {
                // must rest at previous stop
                restsUsed++;
                lastStop = t[i - 1];

                // if even after resting it's still too far, impossible
                if (t[i] - lastStop > D)
                    return false;
            }
        }

        return restsUsed <= k;
    }

    public static void main(String[] args) {
        int failures = 0;

        Object[][] tests = new Object[][]{
            { new int[]{0, 5, 10, 17, 20}, 1, 10 }, // example
            { new int[]{0, 5, 10, 17, 20}, 0, 20 }, // no rests allowed
            { new int[]{0, 5, 10, 17, 20}, 3, 7  }, // can rest at all intermediates
            { new int[]{0, 4, 8, 12}, 1, 8 }, // one rest
            { new int[]{0, 4, 8, 12}, 2, 4 }, // two rests -> max gap
            { new int[]{0, 10}, 0, 10 } // trivial two-stop case
        };

        int i = 0;
        for (Object[] tt : tests) {
            i++;
            int[] stops = (int[]) tt[0];
            int k = (int) tt[1];
            int expected = (int) tt[2];

            int got = minimizeMaxDailyDistance(stops, k);
            if (got == expected) {
                System.out.printf("PASS %d -> %d%n", i, got);
            } else {
                System.out.printf("FAIL %d -> got=%d expected=%d%n", i, got, expected);
                failures++;
            }
        }

        if (failures > 0) {
            System.out.printf("%d test(s) failed.%n", failures);
            System.exit(1);
        } else {
            System.out.println("All tests passed.");
        }
    }
}
