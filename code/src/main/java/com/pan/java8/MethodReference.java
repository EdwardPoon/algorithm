package com.pan.java8;

import java.util.function.Consumer;

public class MethodReference {
	/**
	 * a method reference can't be used for any method. They can only be used to
	 * replace a single-method lambda expression.
	 * 
	 * Instead of using AN ANONYMOUS CLASS you can use A LAMBDA EXPRESSION And if
	 * this just calls one method, you can use A METHOD REFERENCE
	 * 
	 * Consumer<String> c = s -> System.out.println(s); 
	 * can be replaced by 
	 * Consumer<String> c = System.out::println;
	 */
	
	public static void main(String[] args) {
		Mechanic mechanic = new Mechanic();
		Car car = new Car();

		// Using an anonymous class
		execute(car, new Consumer<Car>() {
		  public void accept(Car c) {
		    mechanic.fix(c);
		  }
		});
		
		// Using a lambda expression
		execute(car, c -> mechanic.fix(c));

		// Using a method reference
		execute(car, mechanic::fix);
	}
	public static void execute(Car car, Consumer<Car> c) {
		  c.accept(car);
		}
	
	
	static class Car {
		  private int id;
		  private String color;
		  // More properties
		  // And getter and setters
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getColor() {
			return color;
		}
		public void setColor(String color) {
			this.color = color;
		}
	}
	static class Mechanic {
	  public void fix(Car c) {
	    System.out.println("Fixing car " + c.getId());
	  }
	}
}
