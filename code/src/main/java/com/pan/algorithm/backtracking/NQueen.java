package com.pan.algorithm.backtracking;

public class NQueen {

    public static void main(String... args) {
        new NQueen().cal8queens(0);
    }


    int[] result = new int[8];
    public void cal8queens(int row) {
        if (row == 8) {
            printQueens(result);
            return;
        }
        for (int column = 0; column < 8; ++column) {
            if (isOk(row, column)) {
                result[row] = column;
                cal8queens(row+1);
            }
        }
    }

    private boolean isOk(int row, int column) {
        int leftup = column - 1, rightup = column + 1;
        for (int i = row-1; i >= 0; --i) { // check the previous row
            if (result[i] == column) return false; //check if there is chess in the column of row i
            if (leftup >= 0) { // check letfup  diagonal
                if (result[i] == leftup) return false;
            }
            if (rightup < 8) { // check right up diagonal
                if (result[i] == rightup) return false;
            }
            --leftup; ++rightup;
        }
        return true;
    }

    private void printQueens(int[] result) {
        for (int row = 0; row < 8; ++row) {
            for (int column = 0; column < 8; ++column) {
                if (result[row] == column) System.out.print("Q ");
                else System.out.print("* ");
            }
            System.out.println();
        }
        System.out.println();
    }
}
