package com.pan.algorithm;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.regex.Pattern;

// https://www.hackerrank.com/challenges/30-regex-patterns/tutorial
// https://docs.oracle.com/javase/tutorial/essential/regex/pre_char_classes.html
public class RegularExpressions {

	
	private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int N = scanner.nextInt();
        scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");
        
        LinkedList<String> list = new LinkedList<String>();
        Map<String,String> map = new HashMap<String,String>();
        
        String myRegExString = "@gmail.com$";
        Pattern p = Pattern.compile(myRegExString);

        for (int NItr = 0; NItr < N; NItr++) {
            String[] firstNameEmailID = scanner.nextLine().split(" ");

            String firstName = firstNameEmailID[0];

            String emailID = firstNameEmailID[1];
            map.put(emailID, firstName);
        }
        scanner.close();
        for(Entry<String,String> entry: map.entrySet()){
        	String emailID = entry.getKey();
        	String firstName = entry.getValue();
            if ( p.matcher(emailID).find()){
            	//System.out.println(emailID);
            	if (list.size()>0){
            		int length = list.size();
                	for (int i = 0;i<length;i++){
                		if (firstName.compareTo(list.get(i)) <0){
                			list.add(i, firstName);
                			break;
                		}
                		if (i == list.size()-1){
                			list.add(firstName);
                		}
                	}
            	}else{
            		list.add(firstName);
            	}
            }
        }

        
        for (int i = 0;i<list.size();i++){
        	System.out.println(list.get(i));
        }


        /**
        6
        riya riya@gmail.com
        julia julia@julia.me
        julia sjulia@gmail.com
        julia julia@gmail.com
        samantha samantha@gmail.com
        tanya tanya@gmail.com
        */
    }
}
