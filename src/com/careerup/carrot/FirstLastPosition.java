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
        int low = 0, high = nums.length - 1;
        while(low <= high){
            int mid = low + (high - low) / 2;
            if(target > nums[mid])
                low = mid + 1;
            else
                // lower bound, return low, equals goes to left
                high = mid - 1;
        }
        return low;
    }
    
    private static int upperBound(int[] nums, int target){
        int low = 0, high = nums.length - 1;
        while(low <= high){
            int mid = low + (high - low) / 2;
            // upper bound, return high, equals goes to right
            if(target >= nums[mid])
                low = mid + 1;
            else
                high = mid - 1;
        }
        return high;
    }

    public static void main(String[] args) {
        int[] nums = {1, 2, 2, 2, 3, 4, 5};
        int target = 2;
        int[] result = searchRange(nums, target);
        System.out.println("First position: " + result[0]);
        System.out.println("Last position: " + result[1]);
    }
}
