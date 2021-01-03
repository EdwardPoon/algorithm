package com.pan.multithread;

public class StarvationDemo extends Thread { 
    static int threadcount = 0; 
    static String lock = "";
    public StarvationDemo(String name) {
    	super(name);
    }

	/**
	 * In Starvation, threads are also waiting for each other. But here waiting time
	 * is not infinite after some interval of time, waiting thread always gets the
	 * resources whatever is required to execute thread run() method.
	 */
    public void run(){
        System.out.println("Name:" + this.getName() + ", Thread execution starts"); 
        synchronized(lock) {
        	threadcount++; 
        }
        System.out.println("Name:" + this.getName() + "， threadcount：" +  threadcount); 
        System.out.println("Child thread execution completes"); 
    } 
    public static void main(String[] args) throws InterruptedException { 
        System.out.println("Main thread execution starts"); 
        //Default priority of a thread is 5 (NORM_PRIORITY). The value of MIN_PRIORITY is 1 and the value of MAX_PRIORITY is 10.

        // Thread priorities are set in a way that thread5 
        // gets least priority.
        StarvationDemo thread1 = new StarvationDemo("thread 1");
        thread1.setPriority(10);
        StarvationDemo thread2 = new StarvationDemo("thread 2");
        thread2.setPriority(9);
        StarvationDemo thread3 = new StarvationDemo("thread 3");
        thread3.setPriority(8);
        StarvationDemo thread4 = new StarvationDemo("thread 4");
        thread4.setPriority(7);
        StarvationDemo thread5 = new StarvationDemo("thread 5");
        thread5.setPriority(6);

        thread1.start(); 
        thread2.start(); 
        thread3.start(); 
        thread4.start(); 
  
        // Here thread5 have to wait beacause of the 
        // other thread. But after waiting for some 
        // interval, thread5 will get the chance of  
        // execution. It is known as Starvation 
        thread5.start();
  
        System.out.println("Main thread execution completes"); 
    } 
} 
