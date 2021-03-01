package com.pan.algorithm.dynamicprogramming;

/**
 * given a group of items, what will be the max weight can be put in the bag with a weight limit
 */
public class ZeroOneBag {

    public static void main(String[] args) {
        int weightLimit = 9; // the max weight limit for the bag
        int[] weight = {2,2,4,6,4}; // weight of the items

        ZeroOneBag zeroOneBag = new ZeroOneBag();
        //zeroOneBag.calculateMaxWeight(weight, 0, 0, weightLimit);
        //System.out.println("result=" + zeroOneBag.result);
        System.out.println("result=" + zeroOneBag.knapsack(weight, weightLimit) );
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
    // dynamic programming, O(n * m ), n is the length of the items, m is the weightLimit
    public int knapsack(int[] weight, int weightLimit) {
        int n = weight.length;
        boolean[][] states = new boolean[n][weightLimit+1];// first index is the item index, second index is the weightSum
        states[0][0] = true;
        if (weight[0] <= weightLimit) {
            states[0][weight[0]] = true;
        }
        for (int i = 1; i < n; ++i) {
            for (int j = 0; j <= weightLimit; ++j) {// not putting the i into the bag
                if (states[i-1][j] == true) states[i][j] = states[i-1][j];
            }
            for (int j = 0; j <= weightLimit-weight[i]; ++j) {// put i into the bag
                if (states[i-1][j]==true) states[i][j+weight[i]] = true;
            }
        }
        for (int i = weightLimit; i >= 0; --i) {
            if (states[n-1][i] == true) return i;
        }
        return 0;
    }
}
