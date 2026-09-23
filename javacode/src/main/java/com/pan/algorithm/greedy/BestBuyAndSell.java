package com.pan.algorithm.greedy;

// You want to maximize your profit by choosing a single day to buy one stock
// and choosing a different day in the future to sell that stock.
//Input: prices = [7,1,5,3,6,4]
//Output: 5, buy at 1 and sell at 6
public class BestBuyAndSell {

    public static void main(String[] args) {
        int[] prices = {7,1,5,3,6,4};
        System.out.println( new BestBuyAndSell().maxProfit(prices));
    }
    public int maxProfit(int[] prices) {
        int max = 0;
        int currentMax = 0;
        int i = 0;
        for (int j=1; j < prices.length; j++) {
            if (prices[j] > prices[i]) {
                currentMax = prices[j] - prices[i];
            }
            else {
                i = j;
            }
            max = Math.max(max, currentMax);
        }
        return max;
    }
}
