package com.pan.hello;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Test {

	private static final String str = "";

	public static void main(String[] args) throws Exception{


		System.out.println(1010 % 1000);
		//ss
		
		BigDecimal numerator = BigDecimal.valueOf(170);
		BigDecimal denominator = BigDecimal.valueOf(30);
		System.out.println(numerator.divideToIntegralValue(denominator));
		
		
		Map<String,String> map = new HashMap<String,String>();
		map.put("aa", "1");
		FindOneWithParameterEvent findOneWithParameterEvent = new FindOneWithParameterEvent(map);
		map = new HashMap<String,String>();
		map.put("aa", "2");
		
		System.out.println(map.get("aa"));
		System.out.println(findOneWithParameterEvent.getQueryParameters().get("aa"));
		
		try{
			Integer in = Integer.valueOf("fff");
			System.out.println(in);
		}catch(NumberFormatException e){
			System.out.println(map.get("aa"));
		}
		
		
		System.out.println(map.get("aa"));
		
		
		LinkedList<String> list = new LinkedList<String>();
		
		
		String aa = "1111334333335323";
		char prevChar = '\0'; 
		int maxConseq = 0;
		int conseq = 0;
		char maxConseqChar = '\0';
		StringBuilder conseqBuilder = new StringBuilder("");
		for (char temp : aa.toCharArray()){
			if (temp == prevChar ){
				System.out.println("consecutive");
				conseq++;
			}else{
				System.out.println("not consecutive");
				conseq = 0;
			}
			if (conseq>maxConseq){
				maxConseq = conseq;
				maxConseqChar = temp;
			}
			prevChar = temp;
		}
		
		for (int i=0;i<=maxConseq;i++){
			conseqBuilder.append(maxConseqChar);
		}
		System.out.println(conseqBuilder);
	}
}
