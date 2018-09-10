package com.pan.algorithm;

public class BitwiseAnd {
	
    public static void main(String[] args) {
        String[] stringlist = new String[]{"127 64","8 5","2 2"};

        for (String str : stringlist) {
            String[] nk = str.split(" ");

            int n = Integer.parseInt(nk[0]);
            int k = Integer.parseInt(nk[1]);
            
            
            int max = 0;
            for (int i = k-1;i>0;i--) {
            	for (int j =n;j>0 ;j--) {
            		if (i==j) {
            			continue;
            		}
            		System.out.println(i+",i:"+Integer.toBinaryString(i));
            		System.out.println(j+",j:"+Integer.toBinaryString(j));
            		System.out.println("i & j:" + (i & j));
            		int temp = i & j;
            		if (temp > max) {
            			max = temp;
            		}
            		if ( temp ==k-1) {
            			break;
            		}
            	}
            	if (max == k-1 ) {
            		break;
            	}
            }
            
            
            System.out.println(max);
        }
    }

}
