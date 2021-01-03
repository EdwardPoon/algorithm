package com.pan.multithread;

import java.lang.reflect.Field;
import sun.misc.*;

public class UnsafeTest {

	
	 public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, InstantiationException {
	        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
	        theUnsafe.setAccessible(true);
	        Unsafe unsafe = (Unsafe) theUnsafe.get(null);
	        InnerClass o = (InnerClass)unsafe.allocateInstance(InnerClass.class);
	        o.print(); // print 100
	        Field a = o.getClass().getDeclaredField("value");
	        unsafe.putLong(o, unsafe.objectFieldOffset(a), 10000);
	        o.print(); // print 10000
	        unsafe.compareAndSwapLong(o, unsafe.objectFieldOffset(a), 10000, 1111);
	        o.print(); // print 1111
	        unsafe.compareAndSwapLong(o, unsafe.objectFieldOffset(a), 1000, 10000);
	        o.print(); // print 1111
	    }
	 
	 static class InnerClass {
	        // 保证内存可见性
	        private volatile long value;
	        InnerClass() {
	            value = 100L;
	        }
	        void print() {
	            System.err.println("value==>" + value);
	        }
	    }
}
