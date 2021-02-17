package com.pan.datastructure;

public class Heap {

    // 1. heap is a complete binary tree
    // 2. each node is greater ( max heap) or less (min heap) than it's left children and right children
    public static void main(String[] args) {

        // Binary Tree Representation
        // of input array
        //              1
        //           /     \
        //         3        5
        //      /    \     /  \
        //     4      6   13  10
        //    / \    / \
        //   9   8  15 17
        int arr[] = { 1, 3, 5, 4, 6, 13, 10,
                9, 8, 15, 17 };

        (new Heap()).buildHeap(arr);
        for (int value : arr) {
            System.out.print(value + " ");
        }
        System.out.println(" ");
    }

    // Root is at index 0 in array.
    // Left child of i-th node is at (2*i + 1)th index.
    // Right child of i-th node is at (2*i + 2)th index.
    // Parent of i-th node is at (i-1)/2 index.
    public void buildHeap(int[] array) {
        // Index of last non-leaf node
        int length = array.length;
        int startIdx = (length / 2) - 1;

        // Perform reverse level order traversal
        // from last non-leaf node and heapify
        // each node
        for (int i = startIdx; i >= 0; i--) {
            heapify(array, length, i);
        }

    }

    // To heapify a subtree rooted with node i which is
    // an index in arr[].Nn is size of heap
    private void heapify(int[] arr, int n, int i) {
        int largest = i; // Initialize largest as root
        int l = 2 * i + 1; // left = 2*i + 1
        int r = 2 * i + 2; // right = 2*i + 2

        // If left child is larger than root
        if (l < n && arr[l] > arr[largest])
            largest = l;

        // If right child is larger than largest so far
        if (r < n && arr[r] > arr[largest])
            largest = r;

        // If largest is not root
        if (largest != i) {
            int swap = arr[i];
            arr[i] = arr[largest];
            arr[largest] = swap;

            // Recursively heapify the affected sub-tree
            heapify(arr, n, largest);
        }
    }
}