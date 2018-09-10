package com.pan.java8.interfacefeature;

@FunctionalInterface
public interface Interface1 {
	
	public void println();
	default public void println2(){
		
		System.out.println("");
	}

}
