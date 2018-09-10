package com.pan.java8;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class StreamExample {

	public static void main(String[] args) {
		
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
		// get the sum salary
		Double sum = employeeList.stream().filter(emp -> emp.getAge()>40)
				.map(emp -> emp.getSalary()).reduce(0.00, (a,b)->a+b);
		System.out.println(sum);
		
		//get the max salary
		Optional<Employee> maxSalaryEmp = employeeList.stream()
	    .reduce((Employee a, Employee b) -> a.getSalary() < b.getSalary() ? b:a);
		if(maxSalaryEmp.isPresent())
		  System.out.println("Employee with max salary: "+maxSalaryEmp.get());
		}


}
