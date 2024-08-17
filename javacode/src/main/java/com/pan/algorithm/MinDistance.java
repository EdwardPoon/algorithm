package com.pan.algorithm;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MinDistance {

    // find the min distance
    public static int getMinDistance(int k, List<Integer> arr){
        int distance = 0;
        Collections.sort(arr, new Comparator<Integer>() {
            @Override
            public int compare(Integer num1, Integer num2)
            {

                return  num1.compareTo(num2);
            }
        });
        for (int i = k -1; i < arr.size(); i++){
            int tempDist = arr.get(i) - arr.get(i+1-k);
            if (i == k -1){
                distance = tempDist;
            }else if (tempDist < distance){
                distance = tempDist;
            }
        }
        return distance;
    }

    public static void main(String[] args) throws IOException {

    }
}
