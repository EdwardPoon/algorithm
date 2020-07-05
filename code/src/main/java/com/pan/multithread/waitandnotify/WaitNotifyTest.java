package com.pan.multithread.waitandnotify;

public class WaitNotifyTest {

    public static void main(String[] args) {
        Message msg = new Message("process it");
        //Waiter waiter1 = new Waiter(msg);
        new Thread(() -> {
            //String name = this.toString();
            synchronized(msg) {
                try{
                    System.out.println(" waiting to get notified at time:"+System.currentTimeMillis());
                    msg.wait();
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                System.out.println(" waiter thread got notified at time:"+System.currentTimeMillis());
                //process the message now
                System.out.println(" processed: "+msg.getMsg());
            }
        },"waiter1").start();

        Waiter waiter2 = new Waiter(msg);
        new Thread(waiter2, "waiter2").start();

        Notifier notifier = new Notifier(msg);
        new Thread(notifier, "notifier").start();
        System.out.println("All the threads are started");
    }
}
