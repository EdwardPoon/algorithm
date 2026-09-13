package com.pan.datastructure;

public class CircularQueue {

	  private String[] items;
	  private int n = 0;
	  // head is the index of the head，tail is the index of the tail, enqueue will add item to tail, and dequeue will remove item from head and head++
	  private int head = 0;
	  private int tail = 0;

	  public CircularQueue(int capacity) {
	    items = new String[capacity];
	    n = capacity;
	  }

	  public boolean enqueue(String item) {
	    // the key is how to check if the queue is full,
		// if tail + 1 == head ,then full
	    if ((tail + 1) % n == head) return false;
	    items[tail] = item;
	    tail = (tail + 1) % n;
	    return true;
	  }

	  public String dequeue() {
	    if (head == tail) return null;
	    String ret = items[head];
	    head = (head + 1) % n;
	    return ret;
	  }
	}
