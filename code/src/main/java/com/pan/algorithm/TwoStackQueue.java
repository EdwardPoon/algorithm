package com.pan.algorithm;

import java.util.Scanner;
import java.util.Stack;

public class TwoStackQueue {

	
    public static void main(String[] args) {
        MyQueue<Integer> queue = new MyQueue<Integer>();

        Scanner scan = new Scanner(System.in);
        int n = scan.nextInt();

        for (int i = 0; i < n; i++) {
            int operation = scan.nextInt();
            if (operation == 1) { // enqueue
              queue.enqueue(scan.nextInt());
            } else if (operation == 2) { // dequeue
              queue.dequeue();
            } else if (operation == 3) { // print/peek
              System.out.println(queue.peek());
            }
        }
        scan.close();
    }
    
}

class MyQueue<T> {

	private Stack<T> stack1;
	private Stack<T> stack2;
	
	public MyQueue(){
		stack1 = new Stack<T>();
		stack2 = new Stack<T>();
	}
	
	public void enqueue(T item){
		stack1.push(item);
	}
	public T dequeue(){
		if (stack1.isEmpty())
			return null;
		
		while (!stack1.isEmpty()){
			stack2.push(stack1.pop());
		}
		T res =  stack2.pop();
		while (!stack2.isEmpty()){
			stack1.push(stack2.pop());
		}
		return res;
	}
	public T peek(){
		if (stack1.isEmpty())
			return null;
		while (!stack1.isEmpty()){
			stack2.push(stack1.pop());
		}
		T res =  stack2.peek();
		while (!stack2.isEmpty()){
			stack1.push(stack2.pop());
		}
		return res;
	}
}