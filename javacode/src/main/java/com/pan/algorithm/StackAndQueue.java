package com.pan.algorithm;

import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

public class StackAndQueue {

    private Stack<Character> stack = new Stack<Character>();
    private Queue<Character> queue = new ConcurrentLinkedQueue<Character>();
    
    void pushCharacter(char ch){
        stack.push(ch);
    }
    void enqueueCharacter(char ch){
    	queue.add(ch);
    }
    char popCharacter(){
        return stack.pop();
    }
    char dequeueCharacter(){
    	return queue.remove();
    }
}
