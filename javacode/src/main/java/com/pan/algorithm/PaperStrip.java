package com.pan.algorithm;

/**
 * You are given two paper strips, on each strip, numbers (1,2,... N) are written in random order.
 * Cut the original paper strip into several pieces and rearrange those pieces to form the desired sequence
 * int[] original = new int[] {1, 4, 3, 2};
 * int[] desired = new int[] {1, 2, 4, 3};
 * As it need to cut to {1}, {4, 3}, {2}
 * PaperStrip.minPieces(original, desired) == 3;
 */
public class PaperStrip {

    public static void main(String[] args){
        int[] original = new int[] {1, 4, 3, 2};// 3 = {1}, {4, 3}, {2}
        int[] desired  = new int[] {1, 2, 4, 3};
        System.out.println("minPieces=" + minPieces(original, desired));

        original = new int[] {7, 6, 1, 3, 5, 9, 11, 2, 4, 8, 10}; // 7= {7}, {6}, {1,3}, {5}, {9,11,2}, {4}, {8,10}
        desired = new int[] {1, 3, 9, 11, 2, 7, 5, 8, 10, 4, 6};
        System.out.println("minPieces=" + minPieces(original, desired));

        original = new int[] {1, 3, 9, 11, 2, 7, 5, 8, 10, 4, 6}; // 1
        desired = new int[] {1, 3, 9, 11, 2, 7, 5, 8, 10, 4, 6};
        System.out.println("minPieces=" + minPieces(original, desired));
    }

    public static int minPieces(int[] original, int[] desired){
        int res = 0;
        int i = 0;
        int j;
        int arrayLength = original.length;
        boolean[] checkedFlag = new boolean[arrayLength];
        while (i < desired.length){
            j = 0;
            while (j < original.length){
                if (checkedFlag[j]){
                    j++;
                    continue;
                }
                if (original[j] == desired[i]){
                    while (j < arrayLength && i < arrayLength && original[j] == desired[i]){
                        checkedFlag[j] = true;
                        i++;
                        j++;
                    }
                    res++;
                    //System.out.println("res=" +res +", i=" +i);
                    break;
                }
                j++;
            }
        }
        return res;
    }
}
