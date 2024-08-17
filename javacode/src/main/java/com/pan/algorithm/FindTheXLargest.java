package com.pan.algorithm;

//Find the x largest number in the array
//In array 4， 2， 5， 12， 3, the 3rd largest number is 4. how to implement it with time complexity O(n)

import com.pan.algorithm.sort.Sort;

public class FindTheXLargest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Comparable<Integer>[] arr = new Integer[]{1, 7, 3, 8, 2, 5, 6, 4};

		int x = 3;// the 3 largest
		Comparable<Integer> res = findXLargest(arr, x);
		System.out.println("the " + x + " Largest is " +res);
	}
	// if find the x largest(smallest) element by quick sort, the time complexity is n + n/2 + n/4 + n/8 ....
	// so it equal to 2n-1, O(n)
	private static Comparable findXLargest(Comparable[] array, int x) {

		int start = 0;
		int end = array.length - 1;
		int k = partition(array, start, end);
		while (k + 1 != x) {
			if (k + 1 < x) {
				start = k + 1;
				//k = sort( array, k, end, x);
			} else {
				end = k - 1;
				//k = sort( array, start, k, x);
			}
			System.out.println("start:" + start + ", end:" + end + ", k:" + k);
			k = partition(array, start, end);
		}
		return array[k];
	}

	// use the end as pivot to split the array into two parts(smaller and larger),return the index of the pivot after split
	private static int partition(Comparable[] array, int start, int endIndex) {

		int i = start;
		for (int j = start; j < endIndex; j++) {
			if (Sort.less(array[endIndex], array[j])) { // only need to swap this to Sort.less(array[j], array[endIndex]) will get the x smallest number
				Sort.exch(array, i, j);
				System.out.println("Exchange, i:" + i + ", j:" + j + ", endIndex:" + endIndex);
				Sort.show(array);
				i++;
			}
		}
		Sort.exch(array, i, endIndex);
		System.out.println("swap i="+i + ", endIndex=" + endIndex);
		Sort.show(array);
		return i;
	}
}
