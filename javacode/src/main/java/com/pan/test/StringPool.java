package com.pan.test;

public class StringPool {
	
	public static void main(String[] args){
		
        String s1 = "Cat";
        String s2 = "Cat";
        String s3 = new String("Cat");
        String s4 = new String("Cat");
        
        System.out.println("s1 == s2 :"+(s1==s2));
        System.out.println("s1 == s3 :"+(s1==s3));
        System.out.println("s3 == s4 :"+(s3==s4));
        
        
        
        String s5 = "JournalDev";
        int start = 1;
        char end = 5;
        //System.out.println(start + end);
        //System.out.println(s5.substring(start, end));
        
        System.out.println(" ============= ");
        testintern();
	}

	
	public static void testintern() {
        String s1 = "Test";
        String s2 = "Test";
        String s3 = new String("Test");
        final String s4 = s3.intern();

        System.out.println("s1 == s2 :"+(s1 == s2));
        System.out.println("s2 == s3 :"+(s2 == s3));
        System.out.println("s3 == s4 :"+(s3 == s4));
        System.out.println("s1 == s3 :"+(s1 == s3));// false
        System.out.println("s1 == s4 :"+(s1 == s4));// true

        System.out.println("s1.equals(s2) :"+s1.equals(s2));
        System.out.println("s2.equals(s3) :"+s2.equals(s3));
        System.out.println("s3.equals(s4) :"+s3.equals(s4));
        System.out.println("s1.equals(s4) :"+s1.equals(s4));
        System.out.println("s1.equals(s3) :"+s1.equals(s3));
    }
}
