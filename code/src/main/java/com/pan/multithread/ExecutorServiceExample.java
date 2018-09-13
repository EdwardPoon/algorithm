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

	public static void main(String[] args) throws Exception {
		//testRunable();
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
	private static class CallableTask implements Callable<String>{
		
		private int index = 0;
		public CallableTask(int i) {
			this.index= i;
		}

		@Override
		public String call() throws Exception {
			String aa= "task: " + index; 
			System.out.println(aa +" is starting execution!");
			if (index % 2 == 0) {
				TimeUnit.MILLISECONDS.sleep(2000*2);
			}else {
				TimeUnit.MILLISECONDS.sleep(2000);
			}
			
		    System.out.println(aa +" executed!");
			return aa;
		}
		
	}
	// callable and future
	private static void testCallableAndFuture() throws Exception {
		
		ExecutorService executorService = Executors.newFixedThreadPool(3);
		// Runnable tasks can be run using the Thread class or ExecutorService
		// whereas Callables can be run only using the latter.
		// return value is available for Callable
		// Callable can propagate Exception
		/**
		Callable<String> callableTask = () -> {
		    TimeUnit.MILLISECONDS.sleep(300);
		    System.out.println("task Y executed!");
		    return "Task's execution";
		};
		*/
		int i= 0;
		List<Callable<String>> callableTasks = new ArrayList<>();
		while (i<=10) {
			callableTasks.add(new CallableTask(++i));
		}
		
		
		// execute with callable
		// The result of call() method is returned within a Future object:
		List<Future<String>> result = new ArrayList<>();
		for (Callable<String> task:callableTasks) {
			Future<String> future = 
				  executorService.submit(task);
			result.add(future);
		}
		// to ensure the sequence as submit
		for (Future<String> future: result) {
			System.out.println("get result:"+future.get());
		}
		
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
