package com.amazonaws.mtxml;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class HeaderTagTest {
	private static String[] validTags = { "123", "111", "222", "ok", "001", "002", "003" };
	private static String[] validValues = { "SOMETHING", "BLABLA", "noreg", "num321", "1n2n3", "12312542365-64843524",
			"XXX-XXX-XXX?" };
	
	

	/*
	 * Constructor - valid values
	 */
	@ParameterizedTest
	@MethodSource("validValues")
	void testHeaderTagValidValues(String tag, String value) {
		new HeaderTag(tag, value);
	}

	/*
	 * Get tag
	 */
	@ParameterizedTest
	@MethodSource("validValues")
	void testHeaderTagGetTag(String tag, String value) {
		assertEquals(new HeaderTag(tag, value).tag, tag);
	}

	/*
	 * Get value
	 */
	@ParameterizedTest
	@MethodSource("validValues")
	void testHeaderTagGetValue(String tag, String value) {
		assertEquals(new HeaderTag(tag, value).value, value);
	}

	private static Stream<Arguments> validValues() {
		Stream.Builder<Arguments> builder = Stream.builder();
		for (int i = 0; i < validTags.length; i++) {
			builder.add(Arguments.arguments(validTags[i], validValues[i]));
		}

		return builder.build();
	}

	/*
	 * Null arguments
	 */
	@Test
	public void testNullTag() {
		assertThrows(NullPointerException.class, () -> new HeaderTag(null, "somevalue"));
	}

	@Test
	public void testNullValue() {
		assertThrows(NullPointerException.class, () -> new HeaderTag("123", null));
	}

	@Test
	public void testNull() {
		assertThrows(NullPointerException.class, () -> new HeaderTag(null, null));
	}

	/*
	 * Empty arguments
	 */
	@Test
	public void testEmptyTag() {
		assertThrows(IllegalArgumentException.class, () -> new HeaderTag("", "somevalue"));
	}

	@Test
	public void testEmptyValue() {
		assertThrows(IllegalArgumentException.class, () -> new HeaderTag("123", ""));
	}

	@Test
	public void testEmpty() {
		assertThrows(IllegalArgumentException.class, () -> new HeaderTag("", ""));
	}

}
