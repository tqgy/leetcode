package com.careerup.carrot;

public class FirstLastPosition {

    public static int[] searchRange(int[] nums, int target) {
        if(nums == null || nums.length == 0) 
            return new int[]{-1, -1};
        
        int low = lowerBound(nums, target);
        int high = upperBound(nums, target);
        
        // Important check
        if(low < nums.length && low >= 0 && nums[low] != target 
           || high < nums.length && high >= 0 && nums[high] != target)
            return new int[]{-1, -1};
        
        return new int[]{low, high};
    }
    
    private static int lowerBound(int[] nums, int target){
        int low = 0, high = nums.length;
        while(low < high){
            int mid = low + (high - low) / 2;
            if(target > nums[mid])
                low = mid + 1;
            else
                // lower bound, return low, equals goes to left
                high = mid;
        }
        return low;
    }
    
    private static int upperBound(int[] nums, int target){
        int low = 0, high = nums.length;
        while(low < high){
            int mid = low + (high - low) / 2;
            // upper bound, return high, equals goes to right
            if(target >= nums[mid])
                low = mid + 1;
            else
                high = mid;
        }
        return low - 1;
    }

    public static void main(String[] args) {
        System.out.println("=== FirstLastPosition Test Suite ===\n");
        boolean allPassed = true;

        // Basic Cases
        allPassed &= runTest("Basic Multi", new int[]{5, 7, 7, 8, 8, 10}, 8, new int[]{3, 4});
        allPassed &= runTest("Basic Single", new int[]{5, 7, 7, 8, 8, 10}, 6, new int[]{-1, -1});
        allPassed &= runTest("All Same", new int[]{2, 2, 2, 2}, 2, new int[]{0, 3});

        // Edge Cases
        allPassed &= runTest("Empty Array", new int[]{}, 0, new int[]{-1, -1});
        allPassed &= runTest("Null Array", null, 0, new int[]{-1, -1});
        allPassed &= runTest("Single Found", new int[]{1}, 1, new int[]{0, 0});
        allPassed &= runTest("Single Not Found", new int[]{1}, 0, new int[]{-1, -1});

        // Boundary Cases
        allPassed &= runTest("Start of Array", new int[]{1, 2, 3}, 1, new int[]{0, 0});
        allPassed &= runTest("End of Array", new int[]{1, 2, 3}, 3, new int[]{2, 2});

        // Negative Cases
        allPassed &= runTest("Too Small", new int[]{5, 7, 8}, 1, new int[]{-1, -1});
        allPassed &= runTest("Too Large", new int[]{5, 7, 8}, 10, new int[]{-1, -1});
        allPassed &= runTest("Gap in Middle", new int[]{5, 8, 10}, 6, new int[]{-1, -1});

        System.out.println("\nOverall Status: " + (allPassed ? "✅ ALL PASSED" : "❌ SOME FAILED"));
    }

    private static boolean runTest(String name, int[] nums, int target, int[] expected) {
        int[] result = searchRange(nums, target);
        boolean passed = result[0] == expected[0] && result[1] == expected[1];
        
        String resStr = java.util.Arrays.toString(result);
        String expStr = java.util.Arrays.toString(expected);

        System.out.printf("[%s] %-18s | Target: %-3d | Expected: %-8s | Got: %-8s\n", 
            passed ? "PASS" : "FAIL", name, target, expStr, resStr);
        return passed;
    }
}
