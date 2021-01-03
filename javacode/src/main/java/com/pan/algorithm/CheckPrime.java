package com.pan.algorithm;

import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class CheckPrime {

	private static Set<Integer> primeList = new TreeSet<Integer>();
	private static int maxCheckedValue = 2;
	
	private static boolean isPrime2(int n) {
		
        if (n <= 1){
            return false;
        }
        if (n == 2){
            return true;
        }
	    //check if n is a multiple of 2
	    if (n%2==0) return false;
	    //if not, then just check the odds
	    // only need to check to i*i < n, cause if n can divide by i, n must not small than i*i
	    // since, n already can't divide by i*(i-2), i-2 is the previous i
	    for(int i=3;i*i<=n;i+=2) {
	        if(n%i==0)
	            return false;
	    }
	    return true;
	}
	
	private static boolean isPrime(int input){
		boolean res = false;
		
		if (primeList.size() == 0){
			primeList.add(2);
		}
		
		if (input>1){
			// if i is not bigger than the biggest in primelist, then check if it's in list
			if ( input <= maxCheckedValue){
				if (primeList.contains(input)){
					res = true;
				}
			}else{
				res = calculatePrimeList(input);
			}
		}
		return res;
	}
	
	private static boolean calculatePrimeList(int input){
		boolean res = false;
		for (int i=maxCheckedValue+1; i<=input; i++){
			boolean iIsPrime = true;
			
			for (int prime:primeList){
				if (prime > i/2 ){
					break;
				}
				
				if (i % prime ==0){
					iIsPrime = false;
					break;
				}
			}
			if (iIsPrime){
				primeList.add(i);
			}
			
			if (i == input){
				res = iIsPrime;
			}
		}
		maxCheckedValue = input;
		return res;
	}
    public static void main(String[] args) {
    	//input on console:
    	//3
    	//1000000007
    	//100000003
    	//1000003
    	
        Scanner sc=new Scanner(System.in);
        
        int T=sc.nextInt();
        while(T-->0){
        	long millis = System.currentTimeMillis();
            boolean res = isPrime2(sc.nextInt());
            long after  = System.currentTimeMillis() - millis;
            if (res){
            	System.out.println("Prime,"+ after);
            }else{
            	System.out.println("Not prime,"+ after);
            }
        }
        
    }
}
