package com.pan.algorithm.dynamicprogramming;

/**
 * Staircase – 1 step, 2 step or 3 steps – what are the Permutation to get out of that
 */
public class StaircasePermutation {

    public static void main(String... args) {
        int steps = 4;
        System.out.println((new StaircasePermutation()).dynamicCountWay(steps));
    }
    // time complexity O(3^n)
    public int calculatePermutation(int steps) {

        if (steps == 0 || steps == 1) {
            return 1;
        } else if (steps == 2) {
            return 2;
        } else {
            return calculatePermutation(steps - 3) + calculatePermutation(steps - 1) + calculatePermutation(steps - 2);
        }

    }
    // use an array, Time Complexity: O(n) ,Space Complexity: O(n)
    public int dynamicCountWay(int steps) {
        int[] ways = new int[steps +1];
        ways[0] =1;
        ways[1] =1;
        ways[2] =2;
        for (int i = 3; i <= steps; i++)
            ways[i] = ways[i - 1] + ways[i - 2]
                    + ways[i - 3];

        return ways[steps];
    }
}
