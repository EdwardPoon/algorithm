package com.pan.test;

public class Calculator {

    public int power(int n,int p) throws Exception{
    	
    	if (n<0 ||p<0){
    		throw new Exception("n and p should be non-negative");
    	}
        int res = 1;
        for (int i=0;i<p;i++){
        	res = n*res;
        }
        return res;
    }
}
