package com.pan.algorithm.dynamicprogramming;

/**
 * given a group of items, what will be the max weight can be put in the bag with a weight limit
 */
public class ZeroOneBag {

    public static void main(String[] args) {
        int weightLimit = 9; // the max weight limit for the bag
        int[] weight = {2,2,4,6,4}; // weight of the items

        ZeroOneBag zeroOneBag = new ZeroOneBag();
        zeroOneBag.calculateMaxWeight(weight, 0, 0, weightLimit);
        System.out.println("result=" + zeroOneBag.result);
    }

    public int result = 0;

    public void calculateMaxWeight(int[] items, int i, int currentWeight, int weightLimit) {
        System.out.println("i = " + i + ", currentWeight = " + currentWeight );

        if (currentWeight == weightLimit || i == items.length) {  // as the next "if" is checking currentWeight + items[i] <= weightLimit, so currentWeight is not possible greater than weightLimit
            if (currentWeight > result)
                result = currentWeight;
            return;
        }
        calculateMaxWeight(items,i + 1, currentWeight, weightLimit);
        if (currentWeight + items[i] <= weightLimit) {
            calculateMaxWeight(items,i + 1, currentWeight + items[i], weightLimit);
        }
    }
}
