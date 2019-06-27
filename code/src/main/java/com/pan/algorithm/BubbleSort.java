package com.pan.algorithm;


public class BubbleSort {
	private static void printArray(String s, int[] x) {
        System.out.print(s + " Array: ");
        for(int i : x){
            System.out.print(i + " ");
        }
        System.out.println();
    }

    public static void bubbleSort(int[] x) {
        printArray("Initial", x);

        int endPosition = x.length - 1;
        int swapPosition;

        while( endPosition > 0 ) {
            swapPosition = 0;

            for(int i = 0; i < endPosition; i++) {

                if( x[i] > x[i + 1] ){
                    // Swap elements 'i' and 'i + 1':
                    int tmp = x[i];
                    x[i] = x[i + 1];
                    x[i + 1] = tmp;

                    swapPosition = i;
                } // end if

                printArray("Current", x);
            } // end for
            System.out.println("one loop done,endPosition:"+endPosition);
            endPosition = swapPosition;
        } // end while

        printArray("Sorted", x);
    } // end bubbleSort

    public static void main(String[] args) {
        /**Scanner scan = new Scanner(System.in);
        int n = scan.nextInt(); */
        int[] unsorted = {1,15,3,11,7,2,5,7,13,6};// new int[n];
        /**
        for (int i = 0; i < n; i++) {
            unsorted[i] = scan.nextInt();
        }
        scan.close();
		*/
        //bubbleSort(unsorted);
        
        bubbleSort2(unsorted);
    }
    
    public static void bubbleSort2(int[] arr) {
    	
    	int endPos = arr.length-1;
    	int swapPos = 0;
    	int swapCount = 0;
    	while (endPos > 0){
    		swapPos = 0;
    		for (int i=0;i<endPos;i++){
    			if (arr[i]>arr[i+1]){
    				int j = arr[i+1];
    				arr[i+1] = arr[i];
    				arr[i] = j;
    				swapCount++;
    				
    				swapPos = i;
    			}
    		}
    		endPos = swapPos;
    	}
    	System.out.println("Array is sorted in "+ swapCount +" swaps.");
    	System.out.println("First Element: "+ arr[0]);
    	System.out.println("Last Element: "+ arr[arr.length-1]);
    }
}
