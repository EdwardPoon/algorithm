package com.pan.test;

public class CrunchifyStringToCharArray {

	public static void main(String[] args) {
		String testString = "This Is Test";
		char[] stringToCharArray = testString.toCharArray();
 
		for (char output : stringToCharArray) {
			System.out.println(output);
		}
	}
}
