package com.pan.algorithm;


public class MaxSumSubArray {


    public int getMaxSumSubArray(int[] array) {
        int best = 0;
        int max = 0;
        for (int i = 0; i < array.length; i++) {
            max = Math.max(max, max + array[i]);
            best = Math.max(best, max);
        }
        return best;
    }
}
