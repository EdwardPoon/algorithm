package com.pan.algorithm.sort;

public class MergeSort extends Sort {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Comparable<Integer>[] arr = new Integer[5];
		arr[0] = 2;
		arr[1] = 1;
		arr[2] = 5;
		arr[3] = 4;
		arr[4] = 3;
		
		mergeSort(arr);
		show(arr);
	}

	public static void mergeSort(Comparable[] a)
	{
		Comparable[] aux = new Comparable[a.length]; 
		sort(a, 0, a.length - 1);
	}
	private static void sort(Comparable[] a, int lo, int hi)
	{ // Sort a[lo..hi].
		if (hi <= lo) return;
		int mid = lo + (hi - lo)/2;
		sort(a, lo, mid); // Sort left half.
		sort(a, mid+1, hi); // Sort right half.
		merge(a, lo, mid, hi); // Merge results (code on page 271).
	}
	
	private static void merge(Comparable[] a, int lo, int mid, int hi) {
		// Merge a[lo..mid] with a[mid+1..hi].
		int i = lo, j = mid + 1;
		// Copy a[lo..hi] to aux[lo..hi].
		Comparable[] aux = new Comparable[a.length];
		for (int k = lo; k <= hi; k++)
			aux[k] = a[k];
		// Merge back to a[lo..hi].
		for (int k = lo; k <= hi; k++)
			if (i > mid)
				a[k] = aux[j++];
			else if (j > hi)
				a[k] = aux[i++];
			else if (less(aux[j], aux[i]))
				a[k] = aux[j++];
			else
				a[k] = aux[i++];
	}
}
