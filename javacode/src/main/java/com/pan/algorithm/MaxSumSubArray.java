package com.pan.algorithm;


public class MaxSumSubArray {

    public static void main(String[] args) {
        int[] items = {-2,1,-3,4,-1,2,1,-5,4};
        MaxSumSubArray maxSumSubArray = new MaxSumSubArray();
        System.out.println("maxSumSubArray: " + maxSumSubArray.getMaxSumSubArray(items));
    }

    public int getMaxSumSubArray(int[] items) {
        if (items.length == 0) {
            return 0;
        }
        int currentMax = items[0];
        int max = items[0];
        for (int i = 1; i < items.length; i++) {
            currentMax = Math.max(items[i], currentMax + items[i]);
            max = Math.max(max, currentMax);
        }
        return max;
    }
}
