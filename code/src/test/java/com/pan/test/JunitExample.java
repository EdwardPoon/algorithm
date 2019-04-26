package com.pan.test;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class JunitExample {

	
	@Test
	void groupAssertions() {
	    int[] numbers = {0, 1, 2, 3, 4};
	    assertAll("numbers",
	        () -> assertEquals(numbers[0], 1),
	        () -> assertEquals(numbers[3], 3),
	        () -> assertEquals(numbers[4], 1)
	    );
	}
	
	@Test
	void trueAssumption() {
	    assumeTrue(5 > 1);// the test would only be executed when the expression in assumeTrue is true
	    assertEquals(5 + 2, 7);
	}
	
	@ParameterizedTest(name = "{0} + {1} = {2}")
	@CsvSource({
			"0,    1,   1",
			"1,    2,   3",
			"49,  51, 100",
			"1,  100, 101"
	})
	void add(int first, int second, int expectedResult) {
		assertEquals(expectedResult, first + second,
				() -> first + " + " + second + " should equal " + expectedResult);
	}
}
