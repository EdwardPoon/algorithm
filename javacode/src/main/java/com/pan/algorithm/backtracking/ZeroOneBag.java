package com.pan.algorithm.backtracking;


// Given a bag with capacity=x kg, there are an array of items, how to archive carrying max weight?
// time complexity is: 2^n
public class ZeroOneBag {

    public static void main(String[] args) {
        int weightLimit = 9; // the max weight limit for the bag
        int[] items = {2,2,4,6,4}; // weight of the items
        //int[] items = {10,2,4,6,4};

        ZeroOneBag zeroOneBag = new ZeroOneBag();
        zeroOneBag.findMaxWeightComposition(0, 0, items, items.length -1, weightLimit);
        System.out.println("result=" + zeroOneBag.maxWeightComposition);
    }

    public int maxWeightComposition = Integer.MIN_VALUE; // the answer
    // currentWeight
    // i, iterate to the i item
    // capacity: the weight which the bag can carry ；
    // items: an array storing the weight of each item；n: items count
    // f(0, 0, a, 10, 100)
    public void findMaxWeightComposition(int i, int currentWeight, int[] items, int n, int capacity) {
        // currentWeight==w: bag is full; i==n: checked all the items already
        System.out.println("i=" + i + ", currentWeight="  + currentWeight + ", maxWeightComposition=" + maxWeightComposition);

        if (currentWeight == capacity || i == n) {
            maxWeightComposition = Math.max(maxWeightComposition, currentWeight);
            System.out.println("update maxWeightComposition=" + maxWeightComposition );
            return;
        }
        findMaxWeightComposition(i+1, currentWeight, items, n, capacity); // not to put i in
        if (currentWeight + items[i] <= capacity) {// put i in
            findMaxWeightComposition(i+1,currentWeight + items[i], items, n, capacity);
        }
    }
}
