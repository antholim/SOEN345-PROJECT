package com.example.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApplicationTests {

	@Test
	void testAdditionFirstCase() {
		assertEquals(4, 2 + 2);
	}

	@Test
	void testAdditionSecondCase() {
		int result = 2 + 2;
		assertEquals(4, result, "2 + 2 should equal 4");
	}

}