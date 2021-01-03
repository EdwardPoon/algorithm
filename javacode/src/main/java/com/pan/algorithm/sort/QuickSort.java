package com.pan.algorithm.sort;

public class QuickSort extends Sort {


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Comparable<Integer>[] arr = new Integer[8];
		arr[0] = 6;
		arr[1] = 1;
		arr[2] = 5;
		arr[3] = 4;
		arr[4] = 7;
		arr[5] = 3;
		arr[6] = 2;
		arr[7] = 8;
		sort(arr);
		show(arr);
	}

	public static void sort(Comparable[] a) {

		sort(a, 0, a.length - 1);
	}

	private static void sort(Comparable[] a, int lo, int hi) {
		if (hi <= lo)
			return;
		int j = partition(a, lo, hi);
		sort(a, lo, j - 1); // Sort left part a[lo .. j-1].
		sort(a, j + 1, hi); // Sort right part a[j+1 .. hi].
	}
	 // Partition into a[lo..i-1], a[i], a[i+1..hi].
	private static int partition(Comparable[] a, int lo, int hi) {

		// left and right scan indices
		int i = lo, j = hi + 1;
		Comparable v = a[lo]; // partitioning item
		// Scan right, scan left, check for scan complete, and exchange.
		while (true) {
			while (less(a[++i], v)) // increase i, until a[i] is bigger than v
				if (i == hi)
					break;
			while (less(v, a[--j]))// decrease j, until a[j] is less than v
				if (j == lo)
					break;
			if (i >= j)
				break;
			exch(a, i, j);
		}
		// after finish while, 
		
		// Put v = a[j] into position
		exch(a, lo, j);
		return j;// with a[lo..j-1] <= a[j] <= a[j+1..hi].
	}
}
