package com.pan.algorithm.binarytree;

public class BinarySearch {

    public static void main(String[] args){
        int[] arr = new int[12];
        arr[0] = 1;
        arr[1] = 2;
        arr[2] = 3;
        arr[3] = 4;
        arr[4] = 5;
        arr[5] = 6;
        arr[6] = 7;
        arr[7] = 7;
        arr[8] = 7;
        arr[9] = 7;
        arr[10] = 9;
        arr[11] = 9;
        System.out.println("index="+binarySearchTheFirst(arr, 7));
    }

    private static int binarySearch(int[] array, int value) {
        int low = 0;
        int high = array.length - 1;

        while (low <= high){
            int mid = low + ((high - low) >> 1);
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
