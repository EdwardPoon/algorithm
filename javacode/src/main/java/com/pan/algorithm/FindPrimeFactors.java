package com.pan.algorithm;

import java.util.ArrayList;
import java.util.List;

public class FindPrimeFactors {

    public static void main(String... args) {

        System.out.println(Math.sqrt(35));
        System.out.println((new FindPrimeFactors()).findPrimeFactors(791));

    }

    public List<Integer> findPrimeFactors(int number) {
        List<Integer> factors = new ArrayList<Integer>();
        while (number % 2 == 0) {
            factors.add(2);
            number = number /2;

        }
        // Every composite number has at least one prime factor less than or equal to square root of itself.

        for (int i = 3; i <= Math.sqrt(number); i=i+2) {
            System.out.println("i="+i);
            while (number % i == 0) {
                factors.add(i);

                number /= i;
                System.out.println("number="+number);

            }

        }
        // add the remain number which is the result of divided by last i
        if (number > 2)
            factors.add(number);

        return factors;
    }
}
