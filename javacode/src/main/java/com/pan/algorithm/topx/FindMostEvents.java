package com.pan.algorithm.topx;

// find most events within a given window
// you are given an array of event timestamps sorted in ascending order by second
// find the max number of events within 60 seconds
// A window of length 60 includes x to x + 60
// Input ([1, 2, 3, 61, 62],)
// Output 4
public class FindMostEvents {

    public static void main(String[] args) {
        int max = maxEventsInWindow(new int[]{1, 2, 3, 61, 62});
        System.out.println(" maxEventsInWindow: " + max);
    }

    public static int maxEventsInWindow(int[] timestamps) {
        // timestamps is non-decreasing (seconds)
        // Return the max number of events in any contiguous 60-second window (inclusive).
        int maxNum = 0;
        int start = 0;
        for (int end = 0; end < timestamps.length; end++) {
            if (start < end && timestamps[end] - timestamps[start] > 60) {
                start++;
                continue;
            }
            maxNum = Math.max(maxNum, end - start + 1);
        }
        return maxNum;
    }
}
