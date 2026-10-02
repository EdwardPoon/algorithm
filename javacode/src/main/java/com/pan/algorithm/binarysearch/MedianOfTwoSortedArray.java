package com.pan.algorithm.binarysearch;

public class MedianOfTwoSortedArray {
    // nums1 = [1,3,4,5,6], nums2 = [2,9,10,11]
    // merged = [1,2,3,4,5,6,9,10,11], median = 5
    // nums1 = [1,3,4], nums2 = [9,10,11]
    // merged = [1,3,4,9,10,11], median = 3+9/2=6.5
    // test cases are in MedianOfTwoSortedArrayTest

    public static void main(String[] args) {
        System.out.println((double) (3+2) /2);
    }


    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        double median = 0.0;
        int[] longer;
        int[] shorter;
        if (nums1.length >= nums2.length) {
            longer = nums1;
            shorter = nums2;
        }
        else {
            shorter = nums1;
            longer =  nums2;
        }
        int sizeOfLonger = longer.length;
        int sizeOfShorter = shorter.length;
        int lengthDiff = sizeOfLonger - sizeOfShorter;
        // odd number or even number
        boolean isOdd = (sizeOfLonger + sizeOfShorter) % 2 != 0;
        // if all the items in one array is larger or smaller than another
        if (longer[sizeOfLonger-1] <= shorter[0]) {
            //4, 2 = 3
            //4, 3 = 4
            int midPointIndex = sizeOfLonger - (lengthDiff/2);
            midPointIndex--;
            if (isOdd) {
                return longer[midPointIndex];
            }
            else {
                if (midPointIndex + 1 >= sizeOfLonger ) {
                    return (double) (longer[midPointIndex] + shorter[0]) /2;
                }else {
                    return (double) (longer[midPointIndex] + longer[midPointIndex + 1]) /2;
                }
            }
        } else if (shorter[sizeOfShorter-1] <= longer[0]) {
            int midPointIndex = lengthDiff/2;
            if (isOdd) {
                return longer[midPointIndex];
            }
            else {
                if (midPointIndex - 1 <= 0 ) {
                    return (double) (longer[midPointIndex] + shorter[sizeOfShorter-1]) /2;
                }else {
                    return (double) (longer[midPointIndex-1] + longer[midPointIndex]) /2;
                }
            }
        }
        // binary search
        int low = 0;
        int high = sizeOfLonger - 1;

        while (low <= high) {
            int mid = low + ((high - low) >> 1);
            //int mid2 =


        }


        return median;
    }
}
