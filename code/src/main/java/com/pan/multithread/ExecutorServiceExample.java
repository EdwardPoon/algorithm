package com.pan.multithread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

// https://www.baeldung.com/java-executor-service-tutorial

public class ExecutorServiceExample {

	public static void main(String[] args) {
		testRunable();
		testCallableAndFuture();
		
	}
	private static void testRunable() {
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		
		/**
		ExecutorService executorService = 
				  new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,   
				  new LinkedBlockingQueue<Runnable>());
				  */
		
		Runnable runnableTask = () -> {
		    try {
		        TimeUnit.MILLISECONDS.sleep(300);
		        System.out.println("task X executed!");
		    } catch (InterruptedException e) {
		        e.printStackTrace();
		    }
		};
		
		// execute one
		executorService.execute(runnableTask);
		// ExecutorService will not be automatically destroyed when there is not task to process. 
		// It will stay alive and wait for new work to do.
		// shutdown() would shut down after all running threads finish their current work.
		executorService.shutdown();
		try {
		    if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
		        executorService.shutdownNow();
		    } 
		} catch (InterruptedException e) {
		    executorService.shutdownNow();
		}
	}
	// callable and future
	private static void testCallableAndFuture() {
		
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		// Runnable tasks can be run using the Thread class or ExecutorService
		// whereas Callables can be run only using the latter.
		// return value is available for Callable
		// Callable can propagate Exception
		Callable<String> callableTask = () -> {
		    TimeUnit.MILLISECONDS.sleep(300);
		    System.out.println("task Y executed!");
		    return "Task's execution";
		};
		 
		List<Callable<String>> callableTasks = new ArrayList<>();
		callableTasks.add(callableTask);
		callableTasks.add(callableTask);
		callableTasks.add(callableTask);
		
		// execute with callable
		// The result of call() method is returned within a Future object:
		Future<String> future = 
				  executorService.submit(callableTask);
		
		// ExecutorService will not be automatically destroyed when there is not task to process. 
		// It will stay alive and wait for new work to do.
		// shutdown() would shut down after all running threads finish their current work.
		executorService.shutdown();
		try {
		    if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
		        executorService.shutdownNow();
		    } 
		} catch (InterruptedException e) {
		    executorService.shutdownNow();
		}
	}
}
