package com.pan.test;

public class IntegerPool {
	
	//any auto-boxing operation will always use the pool if it's applicable for that value
	// If the value p being boxed is true, false, a byte, or a char in the range \u0000 to \u007f, 
	//or an int or short number between -128 and 127 (inclusive), 
	//then let r1 and r2 be the results of any two boxing conversions of p. It is always the case that r1 == r2.
	//https://docs.oracle.com/javase/specs/jls/se7/html/jls-5.html#jls-5.1.7
	public static void main(String[] args){
		
		int x = 10;
		int y = x + 1;
		Integer z = y; // Not a compile-time constant!
		Integer constant = 11;
		System.out.println(z == constant); // true; reference comparison
		
		
		//we should not  use the integer as the lock 
		Integer x1 = Integer.valueOf(127);  // auto-boxing is using Integer.valueOf
		Integer y1 = Integer.valueOf(127);
		System.out.println(x1 == y1); // true
		
		Integer x3 = new Integer(127);
		Integer y3 = new Integer(127);
		System.out.println(x3 == y3); // false
		
		Integer x2 = Integer.valueOf(128);
		Integer y2 = Integer.valueOf(128);
		System.out.println(x2 == y2); // true
	}
}
