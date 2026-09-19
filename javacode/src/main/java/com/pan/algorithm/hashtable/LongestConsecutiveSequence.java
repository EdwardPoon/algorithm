package com.pan.algorithm.hashtable;


// Input: nums = [100,4,200,1,3,2]
// Output: 4 : [1,2,3,4]

// Input: nums = [0,3,7,2,5,8,4,6,0,1]
// Output: 9

// Input: nums = [1,0,1,2]
// Output: 3

// Input: nums = [-1,1,0,1,2]
// Output: 4

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

// 0 <= nums.length <= 10^5
//-10^9 <= nums[i] <= 10^9
// https://leetcode.com/problems/longest-consecutive-sequence/description/
// solve it with O(n)
public class LongestConsecutiveSequence {

    public static void main(String[] args) {
        LongestConsecutiveSequence longestSeq = new LongestConsecutiveSequence();
        //int[] nums = {100,4,200,1,3,2};
        //int[] nums = {0,3,7,2,5,8,4,6,0,1};
        int[] nums = {-1,1,0,1,2};
        System.out.println("longest:" + longestSeq.longestConsecutive2(nums));
    }

    public int longestConsecutive(int[] nums) {
        int longest = 0;

        BitSet ranges = new BitSet();
        BitSet negativeRanges = new BitSet();
        for (int i : nums) {
            if (i >= 0) {
                ranges.set(i);
            }
            else {
                negativeRanges.set(-i);
            }
        }
        int last = 0;
        boolean firstItem = true;
        while (last < ranges.length()) {
            int set = ranges.nextSetBit(last);
            if (set < 0) {
                break; // No more set bits
            }
            int clear = ranges.nextClearBit(set);
            longest = Math.max(longest, clear - set);

            if (firstItem && set == 0 && negativeRanges.nextSetBit(0) == 1) {
                longest += negativeRanges.nextClearBit(1) - 1;
            }

            last = clear;
            firstItem = false;
        }
        last = 0;
        while (last < negativeRanges.length()) {
            int set = negativeRanges.nextSetBit(last);
            if (set < 0) {
                break; // No more set bits
            }
            int clear = negativeRanges.nextClearBit(set);
            longest = Math.max(longest, clear - set);
            last = clear;
        }
        return longest;
    }

    public int longestConsecutive2(int[] nums) {
        int max = 0;

        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < nums.length; i++) {
            set.add(nums[i]);
        }

        for (int i = 0; i < nums.length; i++) {
            int count = 1;

            // look left
            int num = nums[i];
            while (set.contains(--num)) {
                count++;
                set.remove(num);
            }

            // look right
            num = nums[i];
            while (set.contains(++num)) {
                count++;
                set.remove(num);
            }

            max = Math.max(max, count);
        }

        return max;
    }
}
