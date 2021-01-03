package com.pan.datastructure;

public class CircularQueue {
	  // 数组：items，数组大小：n
	  private String[] items;
	  private int n = 0;
	  // head 表示队头下标，tail 表示队尾下标, enqueue will add item to tail, and dequeue will remove item from head and head++
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
	    // 如果 head == tail 表示队列为空
	    if (head == tail) return null;
	    String ret = items[head];
	    head = (head + 1) % n;
	    return ret;
	  }
	}
