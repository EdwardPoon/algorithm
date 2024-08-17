package com.pan.algorithm.dynamicprogramming;

// Calculate the N’th Fibonacci number.
public class NthFibonacci {

    public static void main(String... args) {
        int n = 6;
        System.out.println((new NthFibonacci()).findNthFibonacciDynamic(n));
    }
    // time complexity O(2^n)
    public int findNthFibonacci(int n) {
        if (n == 0) {
            return 0;
        } else if (n ==1) {
            return 1;
        } else {
            return findNthFibonacci(n -1 ) + findNthFibonacci(n -2 );
        }
    }
    // time complexity O(n)
    public int findNthFibonacciDynamic(int n) {
        if (n == 0){
            return 0;
        }
        if (n == 1){
            return 1;
        }
        int[] fibonacci = new int[n+1];
        fibonacci[0] = 0;
        fibonacci[1] = 1;
        for (int i = 2;i <= n; i++) {
            fibonacci[i] = fibonacci[i-1] + fibonacci[i-2];
        }
        return fibonacci[n];
    }
}
