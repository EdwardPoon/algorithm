package com.pan.java8;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamExample {

	public static void main(String[] args) {
		
		testMap();
		
		List<Integer> myList = new ArrayList<>();
		for(int i=0; i<100; i++) myList.add(i);
		
		//sequential stream
		Stream<Integer> sequentialStream = myList.stream();
		
		//parallel stream
		Stream<Integer> parallelStream = myList.parallelStream();
		
		//using lambda with Stream API, filter example
		Stream<Integer> highNums = parallelStream.filter(p -> p > 90);
		//using lambda in forEach
		highNums.forEach(p -> System.out.println("High Nums parallel="+p));
		
		Stream<Integer> highNumsSeq = sequentialStream.filter(p -> p > 90);
		highNumsSeq.forEach(p -> System.out.println("High Nums sequential="+p));
		
		
		List<Employee> employeeList = Arrays.asList(
			      new Employee("Tom Jones", 45, 7000.00),
			      new Employee("Harry Major", 25, 10000.00),
			      new Employee("Ethan Hardy", 65, 8000.00),
			      new Employee("Nancy Smith", 22, 12000.00),
			      new Employee("Deborah Sprightly", 29, 9000.00));
		// print the salary of the employee with age > 40
		employeeList.stream().filter(emp -> emp.getAge()>40)
			.map(emp -> emp.getSalary())
			.forEach(p -> System.out.println(p));
		// get the sum salary with age > 40
		Double sum = employeeList.stream().filter(emp -> emp.getAge()>40)
				.map(emp -> emp.getSalary()).reduce(0.00, (a,b)->a+b);
		System.out.println(sum);
		
		//get the max salary
		Optional<Employee> maxSalaryEmp = employeeList.stream()
	    .reduce((Employee a, Employee b) -> a.getSalary() < b.getSalary() ? b:a);
		if(maxSalaryEmp.isPresent())
		  System.out.println("Employee with max salary: "+maxSalaryEmp.get());
	}

	// stream().map() lets you convert an object to something else
	private static void testMap() {
		List<String> alpha = Arrays.asList("a", "b", "c", "d");
		List<String> collect = alpha.stream().map(String::toUpperCase).collect(Collectors.toList());
        System.out.println(collect); //[A, B, C, D]
        
        
        List<Integer> num = Arrays.asList(1,2,3,4,5);
        // the collect method modifies, or mutates, an existing value
        List<Integer> collect1 = num.stream().map(n -> n * 2).collect(Collectors.toList());
        System.out.println(collect1); //[2, 4, 6, 8, 10]
	}
	
	private static void testReduce() {
		//identity: The identity element is both the initial value of the reduction and the default result if there are no elements in the stream.
		//In this example, the identity element is 0; 
		//this is the initial value of the sum of ages and the default value if no members exist in the collection roster.

		//accumulator: The accumulator function takes two parameters: a partial result of the reduction
		//(in this example, the sum of all processed integers so far) 
		//and the next element of the stream (in this example, an integer). 
		//It returns a new partial result. In this example, 
		//the accumulator function is a lambda expression that adds two Integer values and returns an Integer value:
		List<Employee> employeeList = Arrays.asList(
			      new Employee("Tom Jones", 45, 7000.00),
			      new Employee("Harry Major", 25, 10000.00),
			      new Employee("Ethan Hardy", 65, 8000.00),
			      new Employee("Nancy Smith", 22, 12000.00),
			      new Employee("Deborah Sprightly", 29, 9000.00));
		// calculate sum salary of people with age > 40
		Double sum = employeeList.stream().filter(emp -> emp.getAge()>40)
				.map(emp -> emp.getSalary()).reduce(0.00, (a,b)->a+b);
	}
}
