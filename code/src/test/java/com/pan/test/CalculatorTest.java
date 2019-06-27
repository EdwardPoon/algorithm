package com.pan.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CalculatorTest {
	
	
	@BeforeAll
	public static void setup() {
		
	}

	@Test
	public void calculatorPower() throws Exception {
		
		Calculator calculator = new Calculator();
		assertEquals(8,calculator.power(2, 3));
		
	}
}
