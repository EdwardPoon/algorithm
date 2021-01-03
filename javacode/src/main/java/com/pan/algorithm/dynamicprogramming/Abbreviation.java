package com.pan.algorithm.dynamicprogramming;

public class Abbreviation {

    // Complete the abbreviation function below.
    // https://www.hackerrank.com/challenges/abbr/problem
    static String abbreviation(String a, String b) {
        // pseudocode
        char[] charA = a.toCharArray();
        char[] charB = b.toCharArray();
        int[] flagA = new int[charA.length];
        int[] flagB = new int[charB.length];
        // all the upper case char in a can be found in b,
        int lastIndexOfB = 0;
        boolean allUpperCharFound = true;
        for (int i = 0; i < charA.length; i++) {

            boolean upperCharfound = false;
            if (Character.isUpperCase(charA[i])) {
                for (; lastIndexOfB < charB.length; lastIndexOfB++) {
                    if (charA[i] == charB[lastIndexOfB]) {
                        flagB[lastIndexOfB] = i + 1;
                        flagA[i] = lastIndexOfB + 1;
                        upperCharfound = true;
                        break;
                    }
                }
                if (!upperCharfound) {
                    allUpperCharFound = false;
                    break;
                }
            }
        }
        if (!allUpperCharFound) {
            return "NO";
        }

        // a = abbAbb
        // a = BAB
        // flagA = [0,0,0,2,0,0]
        // flagB = [0,4,0]
        int lastIndexOfA = 0;
        int previousMatchIndexB = 0;
        for (int i = 0; i < charB.length; i++) {
            if (flagB[i] == 0) {
                boolean upperCharfound = false;
                for (int j = previousMatchIndexB; j < flagA.length && flagA[j] == 0; j++) {
                    lastIndexOfA = j;
                    if (charB[i] == Character.toUpperCase(charA[j])) {
                        upperCharfound = true;
                        continue;
                    }
                }
                if (!upperCharfound) {
                    allUpperCharFound = false;
                    break;
                }
            } else {
                previousMatchIndexB = flagB[i];
            }
        }
        if (!allUpperCharFound) {
            return "NO";
        } else {
            return "YES";
        }
    }
}
