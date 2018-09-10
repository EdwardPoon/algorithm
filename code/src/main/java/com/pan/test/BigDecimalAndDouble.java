package com.pan.test;

public class BigDecimalAndDouble {
	
	public static void main(String[] args){
		
		// https://stackoverflow.com/questions/322749/retain-precision-with-double-in-java?noredirect=1&lq=1
		double total = 0;
		double number2 = 5.6;
		double number3 = 5.8;
        total += number2;
        total += number3;
        System.out.println("number2:"+number2);
        System.out.println("total:"+total);  //11.399999999999999
        
        // need to use BigDecimal when you want to get exact 11.4 
        
        // how float work in java: https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-4.2.3
        // https://stackoverflow.com/questions/322749/retain-precision-with-double-in-java?noredirect=1&lq=1
        //a double-precision floating point value such as the double type is a 64-bit value, where:

        //  1 bit denotes the sign (positive or negative).
        // 11 bits for the exponent.
        // 52 bits for the significant digits (the fractional part as a binary).
	}


}
