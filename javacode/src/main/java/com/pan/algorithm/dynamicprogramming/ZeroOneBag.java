package com.pan.algorithm.dynamicprogramming;

/**
 * given a group of items, what will be the max weight can be put in the bag with a weight limit
 */
public class ZeroOneBag {

    public static void main(String[] args) {
        int maxW = 0;
        int weightLimit = 9; // the max weight limit for the bag
        int[] weight = {2,2,4,6,4}; // weight of the items

        //maxW = (new ZeroOneBag()).calculateMaxWeight(weight, 0, 0, weightLimit);

        MaxWeightCalculation calculation = new ZeroOneBag.MaxWeightCalculation();
        calculation.evaluateItem(0, 0);
        System.out.println(calculation.maxW);
    }


    public int calculateMaxWeight(int[] items, int i, int currentWeight, int weightLimit) {
        if (currentWeight == weightLimit || i == items.length) {
            //if (currentWeight > weightLimit)
             //   weightLimit = currentWeight;
            return weightLimit;
        }
        calculateMaxWeight(items,i + 1, currentWeight, weightLimit);
        if (currentWeight + items[i] <= weightLimit) {
            calculateMaxWeight(items,i + 1, currentWeight + items[i], weightLimit);
        }
        return currentWeight;
    }

    static class MaxWeightCalculation {

        public int maxW = Integer.MIN_VALUE;
        private int[] weight = {2, 2, 4, 6, 4};

        private int limit = 9; // limit

        public void evaluateItem(int i, int currentWeight) {
            if (currentWeight == limit || i == weight.length) { // currentWeight==w means full，i==n means end of evaluate
                if (currentWeight > maxW) maxW = currentWeight;
                return;
            }
            evaluateItem(i + 1, currentWeight); // don't put the i item
            if (currentWeight + weight[i] <= limit) {
                evaluateItem(i + 1, currentWeight + weight[i]); // put the i item
            }
            //return maxW;
        }

    }
}
