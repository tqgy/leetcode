package com.carrerup.pratice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class MergeIntervals {
    public List<int[]> merge(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return new ArrayList<int[]>();
        }

        // 按区间的 start 升序排列
        Arrays.sort(intervals, Comparator.comparingInt(a -> a[0]));

        List<int[]> res = new ArrayList<>();
        res.add(intervals[0]);

        for (int i = 1; i < intervals.length; i++) {
            int[] curr = intervals[i];
            // res 中最后一个元素的引用
            int[] last = res.get(res.size() - 1);

            if (curr[0] <= last[1]) {
                // 找到最大的 end
                last[1] = Math.max(last[1], curr[1]);
            } else {
                // 处理下一个待合并区间
                res.add(curr);
            }
        }

        return res;
    }

    // Main function for testing
    public static void main(String[] args) {
        MergeIntervals solution = new MergeIntervals();
        int[][] intervals = {{1, 3}, {2, 6}, {8, 10}, {15, 18}};
        List<int[]> merged = solution.merge(intervals);

        // Print the merged intervals
        for (int[] interval : merged) {
            System.out.println(Arrays.toString(interval));
        }
        // Output:
        // [1, 6]
        // [8, 10]
        // [15, 18]
    }
}
