package com.pan.algorithm.binarysearch;

public class MedianOfTwoSortedArray {
    // nums1 = [1,3,4,5,6], nums2 = [2,9,10,11]
    // merged = [1,2,3,4,5,6,9,10,11], median = 5
    // nums1 = [1,3,4], nums2 = [9,10,11]
    // merged = [1,3,4,9,10,11], median = 3+9/2=6.5
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
        double median = 0.0;
        int[] longer; // array with longer size
        int[] shorter; // array with smaller size
        if (nums1.length >= nums2.length) {
            longer = nums1;
            shorter = nums2;
        }
        else {
            shorter = nums1;
            longer =  nums2;
        }
        int longLength = longer.length;
        int shortLength = shorter.length;
        int lengthDiff = longLength - shortLength;
        // odd number or even number
        boolean isOdd = (longLength + shortLength) % 2 != 0;
        // if all the items in one array is larger or smaller than another
        if (longer[longLength-1] <= shorter[0]) {
            //4, 2 = 3
            //4, 3 = 4
            int midPointIndex = longLength - (lengthDiff/2);
            midPointIndex--;
            if (isOdd) {
                return longer[midPointIndex];
            }
            else {
                if (midPointIndex + 1 >= longLength ) {
                    return (double) (longer[midPointIndex] + shorter[0]) /2;
                } else {
                    return (double) (longer[midPointIndex] + longer[midPointIndex + 1]) /2;
                }
            }
        } else if (shorter[shortLength-1] <= longer[0]) {
            int midPointIndex = lengthDiff/2;
            if (isOdd) {
                return longer[midPointIndex];
            }
            else {
                if (midPointIndex - 1 <= 0 ) {
                    return (double) (longer[midPointIndex] + shorter[shortLength-1]) /2;
                } else {
                    return (double) (longer[midPointIndex-1] + longer[midPointIndex]) /2;
                }
            }
        }
        // binary search
        int low = 0;
        int high = longLength - 1;
        while (low <= high) {
            int mid = low + ((high - low) >> 1);  // split longer array
            int mid2 = ((longLength + shortLength) >> 1) - mid; // based on the index of,
            if (mid2 < 0) {
                mid2 = 0;
            } else if (mid2 >= shortLength) {
                mid2 = shortLength - 1;
            }
            if (mid2 < shortLength && mid2 >= 0) {
                // check
                //longer[mid-1] <
            }

            if (longer[mid] < shorter[mid2 -1]){
                low = mid + 1;
            } else if (longer[mid-1] > shorter[mid2]){
                high = mid - 1;
            } else{
                break;
            }


        }
        return median;
    }
}
