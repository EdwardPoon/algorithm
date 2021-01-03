package com.pan.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.pan.java8.Employee;

public class PassByValue {
	
	public static void main(String[] args) {

		
		Employee emptemp = null;
		Employee empa = new Employee("empa",33,1500.00);
		empa = emptemp;
		emptemp = empa;
		System.out.println("empa:" + empa.toString());
		

		
		
		Balloon red = new Balloon("Red"); //memory reference 50
		Balloon blue = new Balloon("Blue"); //memory reference 100
		
		// it doesn't work when using swap function
		//swap(red, blue);
		
		// it works when using direct code
		/**
		Balloon temp = red;
		red=blue;
		blue=temp;
		*/
		
		// it doesn't works when create another set of reference
		Balloon o1 = red;
		Balloon o2 = blue;
		Balloon temp2 = o1; 
		o1=o2;
		o2=temp2;
		
		// o1.setColor("green"); // if will affect the blue object
		
		System.out.println("red color="+red.getColor());
		System.out.println("blue color="+blue.getColor());
		
		foo(blue);
		System.out.println("blue color="+blue.getColor());
	}

	private static void foo(Balloon balloon) { //baloon=100
		balloon.setColor("Red"); //baloon=100
		balloon = new Balloon("Green"); //baloon=200
		balloon.setColor("Blue"); //baloon = 200
	}

	public static void swap(Object o1, Object o2){ //o1=50, o2=100, 
		Object temp = o1; //temp=50, o1=50, o2=100
		o1=o2; //temp=50, o1=100, o2=100
		o2=temp; //temp=50, o1=100, o2=50
		// but the o1 and o2 are just copies of reference to red and blue object in main method
		// so swap o1 and o2 would not affect red and blue reference in main method
		// since java is always passed by value of reference
		
	}
	//https://www.journaldev.com/4098/java-heap-space-vs-stack-memory
}

class Balloon {

	private String color;

	public Balloon(){}
	
	public Balloon(String c){
		this.color=c;
	}
	
	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
}
