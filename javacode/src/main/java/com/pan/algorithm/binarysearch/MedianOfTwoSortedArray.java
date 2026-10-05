package com.pan.algorithm.binarysearch;

public class MedianOfTwoSortedArray {
    // nums1 = [1,3,4,5,6], nums2 = [2,9,10,11]
    // merged = [1,2,3,4,5,6,9,10,11], median = 5
    // nums1 = [1,3,4], nums2 = [9,10,11]
    // merged = [1,3,4, 9,10,11], median = 4+9/2=6.5
    // test cases are in MedianOfTwoSortedArrayTest

    public static void main(String[] args) {

        //System.out.println((3+2) >> 1);

        int longLength = 9;
        int shortLength = 8;

        int low = 0;
        int high = longLength;

        int mid = low + ((high - low) >> 1);
        int mid2 = ((longLength + shortLength) >> 1) - mid - 1;

        System.out.println("mid=" +mid + " mid2=" +mid2);
    }

    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int length1 = nums1.length;
        int length2 = nums2.length;
        if (length1 > length2) {
            return findMedianSortedArrays(nums2, nums1);
        }
        if (length1 + length2 == 0) {
            return 0.0;
        }
        int halfTotal = (length1 + length2 + 1) >> 1;
        int low = 0;
        int high = length1;
        boolean isEven = (length1 + length2) % 2 == 0;
        while (low <= high) {
            int mid1 = (low + high) >> 1;
            int mid2 = halfTotal - mid1;

            // border case
            int leftMax1 = mid1 == 0 ? Integer.MIN_VALUE : nums1[mid1 - 1];
            int rightMin1 = mid1 == length1 ? Integer.MAX_VALUE : nums1[mid1];

            int leftMax2 = mid2 == 0 ? Integer.MIN_VALUE : nums2[mid2 - 1];
            int rightMin2 = mid2 == length2 ? Integer.MAX_VALUE : nums2[mid2];

            System.out.println("low=" + low + ", high=" + high + ", mid1=" + mid1 + ", mid2=" + mid2
                    + ", leftMax1=" + leftMax1 + ", rightMin1=" + rightMin1
                    + ", leftMax2=" + leftMax2 + ", rightMin2=" + rightMin2);

            if (leftMax1 <= rightMin2 && leftMax2 <= rightMin1) {
                if (isEven) {
                    return (double) (Math.max(leftMax1, leftMax2) + Math.min(rightMin1, rightMin2)) / 2;
                } else {
                    return Math.max(leftMax1, leftMax2);
                }
            } else if (leftMax1 > rightMin2) {
                high = mid1 - 1;
            } else {
                low = mid1 + 1;
            }
        }
        throw new IllegalArgumentException("No median value");
    }
}
