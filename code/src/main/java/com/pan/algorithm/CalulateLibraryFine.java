package com.pan.algorithm;

import java.util.Scanner;

public class CalulateLibraryFine {

	
    public static void main(String[] args) {
    	//input on console:
    	// 9 6 2015
    	// 6 6 2015
    	int fine = 0;
        Scanner scan = new Scanner(System.in);
        int actualDay = scan.nextInt();
        int actualMonth = scan.nextInt();
        int actualYear = scan.nextInt();
        int expectedDay = scan.nextInt();
        int expectedMonth = scan.nextInt();
        int expectedYear = scan.nextInt();
        scan.close();

        int yearDiff = actualYear - expectedYear;
        if (yearDiff==0){
        	int monDiff = actualMonth - expectedMonth;
        	if (monDiff==0){
        		int dayDiff = actualDay - expectedDay;
        		if (dayDiff>0){
        			fine =  15 * dayDiff;
        		}
        	}else if (monDiff>0){
        		fine =  500 * monDiff;
        	}
        	
        }else if (yearDiff > 0 ){
        	fine =  10000;
        }
        System.out.println(fine);
    }
}
