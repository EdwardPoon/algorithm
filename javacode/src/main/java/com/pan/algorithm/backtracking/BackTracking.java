package com.pan.algorithm.backtracking;

public class BackTracking {



    public int maxWeightComposition = Integer.MIN_VALUE; // the answer
    // currentWeight
    // i, iterate to the i item
    // capacity: the weight which the bag can carry ；
    // items: an array storing the weight of each item；n: items count
    // f(0, 0, a, 10, 100)
    public void findMaxWeightComposition(int i, int currentWeight, int[] items, int n, int capacity) {
        if (currentWeight == capacity || i == n) { // currentWeight==w表示装满了;i==n表示已经考察完所有的物品
            if (currentWeight > maxWeightComposition)
                maxWeightComposition = currentWeight;
            return;
        }
        findMaxWeightComposition(i+1, currentWeight, items, n, capacity);
        if (currentWeight + items[i] <= capacity) {// 已经超过可以背包承受的重量的时候，就不要再装了
            findMaxWeightComposition(i+1,currentWeight + items[i], items, n, capacity);
        }
    }
}
