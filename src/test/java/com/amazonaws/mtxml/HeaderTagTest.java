package com.amazonaws.mtxml;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.amazonaws.test.utils.TestCases;

class HeaderTagTest {

	private static ArrayList<String[]> validTags = TestCases.getValidHeaderTags();

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
		for (String[] tagValuePair : validTags) {
			builder.add(Arguments.arguments(tagValuePair[0], tagValuePair[1]));
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
		new HeaderTag("123", "");
	}

	@Test
	public void testEmpty() {
		assertThrows(IllegalArgumentException.class, () -> new HeaderTag("", ""));
	}

}
