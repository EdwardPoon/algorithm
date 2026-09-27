package com.pan.algorithm.binarysearch;

public class BinarySearch {

    public static void main(String[] args){
        int[] arr = {1,2,3,4,5,6,7,7,7,7,9,9};
        System.out.println("index="+binarySearch(arr, 7));
    }

    private static int binarySearch(int[] array, int value) {
        int low = 0;
        int high = array.length - 1;

        while (low <= high){
            int mid = low + ((high - low) >> 1); // can't use (high + low)/2 as if high and low are big enough, the sum could be out of range
            System.out.println("high="+ high+",low="+ low+",mid=" + mid);
            if (array[mid] == value) {
                return mid;
            } else if (array[mid] < value){
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return -1;
    }

    // when there are duplicate number in the array, find the first matching number
    private static int binarySearchTheFirst(int[] array, int value) {

        int low = 0;
        int high = array.length - 1;

        while (low <= high){
            int mid = low + ((high - low) >> 1);
            System.out.println("high="+ high+",low="+ low+",mid=" + mid);
            if (array[mid] < value){
                low = mid + 1;
            } else if (array[mid] > value) {
                high = mid - 1;
            } else {
                if ((mid==0)|| array[mid-1] != value)
                    return mid;
                else
                    high = mid -1;
            }
        }
        return -1;
    }
}
