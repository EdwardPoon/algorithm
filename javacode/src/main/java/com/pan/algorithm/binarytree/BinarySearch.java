package com.pan.algorithm.binarytree;

public class BinarySearch {

    public static void main(String[] args){
        int[] arr = new int[8];
        arr[0] = 1;
        arr[1] = 2;
        arr[2] = 3;
        arr[3] = 4;
        arr[4] = 5;
        arr[5] = 6;
        arr[6] = 7;
        arr[7] = 8;
        System.out.println("index="+binarySearch(arr, 1));
    }

    private static int binarySearch(int[] array, int value) {
        int index = -1;
        int low = 0;
        int high = array.length - 1;

        while (low <= high){
            int mid = low + (high - low) >> 1;
            System.out.println("high="+ high+",low="+ low+",mid=" + mid);
            if (array[mid] == value) {
                return mid;
            } else if (array[mid] < value){
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return index;
    }
}
