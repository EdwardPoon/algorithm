package com.pan.algorithm;

import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;


//find the max number of member in the array  with the difference between each of two are less and equal than 1
public class FindMaxNumber {

    static int pickingNumbers(int[] a) {

        
        TreeMap<Integer,Integer> treeMap = new TreeMap<Integer,Integer>();
        for (int temp : a){
        	Integer count = treeMap.get(temp);
        	if (count == null){
        		count = 1;
        	}else{
        		count++;
        	}
        	treeMap.put(temp, count);
        }
        int mostMax = 0;
        for(Entry<Integer,Integer> entry : treeMap.entrySet()){
        	System.out.println("currentKey:"+entry.getKey());
        	System.out.println("currentvalue:"+entry.getValue());
        	
    		int sumValue = 0;
    		
    		Integer currentKey = entry.getKey();
    		Integer currentvalue = entry.getValue();
            int lowerKeyCount = 0;
            Entry<Integer,Integer> lowerEntry =  treeMap.lowerEntry(currentKey);
            if (lowerEntry != null){
    	        if (Math.abs( lowerEntry.getKey()-currentKey)==1){
    	        	lowerKeyCount = lowerEntry.getValue();
    	        }
            }
            int higherKeyCount = 0;
            Entry<Integer,Integer> higerEntry =  treeMap.higherEntry(currentKey);
            if (higerEntry != null){
    	        if (Math.abs( higerEntry.getKey()-currentKey)==1){
    	        	higherKeyCount = higerEntry.getValue();
    	        }
            }
            sumValue = lowerKeyCount>higherKeyCount?currentvalue+lowerKeyCount:currentvalue+higherKeyCount;
            if (sumValue>mostMax){
            	mostMax = sumValue;
            }
            System.out.println("lowerKeyCount:"+lowerKeyCount);
            System.out.println("higherKeyCount:"+higherKeyCount);
        }
        
        return mostMax;
    }
//73    
//4 2 3 4 4 9 98 98 3 3 3 4 2 98 1 98 98 1 1 4 98 2 98 3 9 9 3 1 4 1 98 9 9 2 9 4 2 2 9 98 4 98 1 3 4 9 1 98 98 4 2 3 98 98 1 99 9 98 98 3 98 98 4 98 2 98 4 2 1 1 9 2 4
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int[] a = new int[n];
        for(int a_i = 0; a_i < n; a_i++){
            a[a_i] = in.nextInt();
        }
        int result = pickingNumbers(a);
        System.out.println(result);
        in.close();
    }
}
