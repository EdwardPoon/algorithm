package com.pan.algorithm;

//Find the x biggest number in the array
//In array 4， 2， 5， 12， 3, the 3rd biggest number is 4. how to implement it with time complexity O(n)

import com.pan.algorithm.sort.Sort;

public class FindTheXbiggest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Comparable<Integer>[] arr =  new Integer[] {1,7,3,2,4,5,6,8};
		
		Comparable<Integer> res = findXBiggest(arr,3);
		System.out.println(res);
	}
	private static Comparable findXBiggest(Comparable[] array, int x) {
		
		int start =0;
		int end = array.length-1;
		int k = partition( array, start, end);
		while ( k+1 != x) {
			 if (k+1 < x) {
				 start = k+1;
				 //k = sort( array, k, end, x);
			 } else {
				 end = k-1;
				 //k = sort( array, start, k, x);
			 }
			 System.out.println("start:" +start+",end:" +end+",k:" +k);
			 k = partition( array, start, end);
		}
		return array[k];
	}
	// use the last item to split the array into two parts( smaller and bigger),return the index
	private static int partition(Comparable[] array, int start, int end) {
		
		int pivot = end;
		int i=0;
		for (int j=0;j<pivot;j++) {
			if ( Sort.less(array[pivot],array[j])) {
				Sort.exch(array, i, j);
				i++;
			}
		}
		Sort.exch(array, i, pivot);
		Sort.show(array);
		return i;
	}
}
