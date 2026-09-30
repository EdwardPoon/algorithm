package com.pan.algorithm.binarysearch;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class MedianOfTwoSortedArrayTest {

    private final MedianOfTwoSortedArray medianOfTwoSortedArray = new MedianOfTwoSortedArray();


    static Stream<Arguments> provideParameters() {
        return Stream.of(
                //arguments(new int[]{1,3,4,5,6}, new int[]{2,9,10,11}, 5.0),
                arguments(new int[]{1,3,4}, new int[]{9,10,11}, 6.5), // all items in longer array is smaller
                arguments(new int[]{1,3,4,5}, new int[]{9,10,11}, 5),
                arguments(new int[]{7,9,10}, new int[]{1,2,3}, 5), // all items in longer array is larger
                arguments(new int[]{7,9,10,11}, new int[]{1,2,3}, 7)
        );
    }


    @ParameterizedTest
    @MethodSource("provideParameters")
    public void testMedianOfTwoSortedArrayTest(int[] nums1, int[] nums2, double expectedRes){

        assertThat(medianOfTwoSortedArray.findMedianSortedArrays(nums1, nums2)).isEqualTo(expectedRes);
    }
}
