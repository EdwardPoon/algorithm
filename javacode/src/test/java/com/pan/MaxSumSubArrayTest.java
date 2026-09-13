package com.pan;

import com.pan.algorithm.MaxSumSubArray;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MaxSumSubArrayTest {

    private MaxSumSubArray maxSumSubArray;

    @BeforeEach
    void setup(){
        maxSumSubArray = new MaxSumSubArray();
    }

    @Test
    void testGetMaxSumSubArray() {
        int[] numbers = {1, 2, 3, 4, 5, -1, 20};
        int max = maxSumSubArray.getMaxSumSubArray(numbers);
        assertEquals(max, 34);
    }
}
