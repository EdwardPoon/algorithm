package com.pan.algorithm;


// Input: nums = [100,4,200,1,3,2]
// Output: 4 : [1,2,3,4]

// Input: nums = [0,3,7,2,5,8,4,6,0,1]
// Output: 9

// Input: nums = [1,0,1,2]
// Output: 3

// Input: nums = [-1,1,0,1,2]
// Output: 4

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

// 0 <= nums.length <= 10^5
//-10^9 <= nums[i] <= 10^9
// https://leetcode.com/problems/longest-consecutive-sequence/description/
// solve it with O(n)
public class LongestConsecutiveSequence {

    public static void main(String[] args) {
        LongestConsecutiveSequence longestSeq = new LongestConsecutiveSequence();
        //int[] nums = {100,4,200,1,3,2};
        int[] nums = {0,3,7,2,5,8,4,6,0,1};
        System.out.println("longest:" + longestSeq.getLongestSeq(nums));
    }

    public int getLongestSeq(int[] nums) {

        int longest = 0;
        int offset = 1000_000_000;

        BitSet ranges = new BitSet();
        for (int i : nums) {
            ranges.set(i+offset, i+offset+1);
        }
        //System.out.println("ranges.length(): " + ranges.length());
        int last = 0;
        while (last < ranges.length()) {
            int set = ranges.nextSetBit(last);
            if (set < 0) {
                break; // No more set bits
            }
            int clear = ranges.nextClearBit(set);
            longest = Math.max(longest, clear - set);
            last = clear;
        }
        return longest;
    }
}
