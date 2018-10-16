package com.pan.multithread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class AsynTaskAndEnsureSequence {

	public static void main(String[] args)  throws Exception{
		// TODO Auto-generated method stub
		testCallableAndFuture();
	}

	private static void testCallableAndFuture() throws Exception {
		
		ExecutorService executorService = Executors.newFixedThreadPool(5);
		// Runnable tasks can be run using the Thread class or ExecutorService
		// whereas Callables can be run only using the latter.
		// return value is available for Callable
		// and Callable can propagate Exception
		/**
		Callable<String> callableTask = () -> {
		    TimeUnit.MILLISECONDS.sleep(300);
		    System.out.println("task Y executed!");
		    return "Task's execution";
		};
		*/
		int i= 0;
		List<Callable<String>> callableTasks = new ArrayList<>();
		while (i<=15) {
			callableTasks.add(new CallableTask(++i));
		}
		
		
		// execute with callable
		// The result of call() method is returned within a Future object:
		List<Future<String>> futureList = new ArrayList<>();
		for (Callable<String> task:callableTasks) {
			Future<String> future = 
				  executorService.submit(task);
			futureList.add(future);
		}
		// to ensure the sequence as submit
		for (Future<String> future: futureList) {
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
	private static class CallableTask implements Callable<String>{
		
		private int index = 0;
		public CallableTask(int i) {
			this.index= i;
		}

		@Override
		public String call() throws Exception {
			String aa= "task: " + index; 
			System.out.println(aa +" is starting execution!");
			// the second one of 5 would take longer time
			//it help to check out the asyn and blocking feature of ExecutorService and Callable
			if (index % 5 == 2) {
				TimeUnit.MILLISECONDS.sleep(2000*2);
			}else {
				TimeUnit.MILLISECONDS.sleep(2000);
			}
			
		    System.out.println(aa +" executed!");
			return aa;
		}
		
	}
}
