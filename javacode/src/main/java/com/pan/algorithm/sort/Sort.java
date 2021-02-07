package com.pan.algorithm.sort;

public class Sort {
	
	public static void main(String[] args) {
		Comparable<Integer>[] arr = new Integer[5];
		arr[0] = 2;
		arr[1] = 1;
		arr[2] = 5;
		arr[3] = 4;
		arr[4] = 3;

		insertSort(arr);
		show(arr);
		
	}
	public static void selectionSort(Comparable[] a) { // Sort a[] into increasing order.
		int N = a.length; // array length
		// the smallest one will be on left after one loop
		for (int i = 0; i < N; i++) { 
			// Exchange a[i] with smallest entry in a[i+1...N).
			//int min = i; // index of minimal entr.
			for (int j = i + 1; j < N; j++)
				if (less(a[j], a[i]))
					exch(a, i, j);
		}
	}

	public static void insertSort(Comparable[] a) { 
		int N = a.length; // array length
		for (int i = 1; i < N; i++) {
			 // Insert a[i] among a[i-1], a[i-2], a[i-3]... ..
			for (int j = i; j > 0 && less(a[j], a[j - 1]); j--)
				exch(a, j, j - 1);
		}
	}
	
	public static void shellSort(Comparable[] a)
	{ 
		int N = a.length;
		int h = 1;
		while (h < N / 3)
			h = 3 * h + 1; // 1, 4, 13, 40, 121, 364, 1093, ...
		while (h >= 1) { // h-sort the array.
			for (int i = h; i < N; i++) { 
				// Insert a[i] among a[i-h], a[i-2*h], a[i-3*h]... .
				for (int j = i; j >= h && less(a[j], a[j - h]); j -= h)
					exch(a, j, j - h);
			}
			h = h / 3;
		}
	}

	public static boolean less(Comparable v, Comparable w) {
		return v.compareTo(w) < 0;
	}

	public static void exch(Comparable[] a, int i, int j) {
		Comparable t = a[i];
		a[i] = a[j];
		a[j] = t;
	}

	public static void show(Comparable[] a) { // Print the array, on a single
												// line.
		for (int i = 0; i < a.length; i++)
			System.out.print(a[i] + " ");
		System.out.println();
	}

	public static void show(int[] a) { // Print the array, on a single
		// line.
		for (int i = 0; i < a.length; i++)
			System.out.print(a[i] + " ");
		System.out.println();
	}

	public static boolean isSorted(Comparable[] a) { // Test whether the array
														// entries are in order.
		for (int i = 1; i < a.length; i++)
			if (less(a[i], a[i - 1]))
				return false;
		return true;
	}
}
