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
	void testHeaderTagValidValues(String tag, String value, String nodeName, String tagNodeName, String valueNodeName) {
		new HeaderTag(tag, value, nodeName, tagNodeName, valueNodeName);
	}

	/*
	 * Get tag
	 */
	@ParameterizedTest
	@MethodSource("validValues")
	void testHeaderTagGetTag(String tag, String value, String nodeName, String tagNodeName, String valueNodeName) {
		assertEquals(new HeaderTag(tag, value, nodeName, tagNodeName, valueNodeName).getTag(), tag);
	}

	/*
	 * Get value
	 */
	@ParameterizedTest
	@MethodSource("validValues")
	void testHeaderTagGetValue(String tag, String value, String nodeName, String tagNodeName, String valueNodeName) {
		assertEquals(new HeaderTag(tag, value, nodeName, tagNodeName, valueNodeName).getValue(), value);
	}

	private static Stream<Arguments> validValues() {
		Stream.Builder<Arguments> builder = Stream.builder();
		for (String[] testCaseData : validTags) {
			String tag = testCaseData[0];
			String value = testCaseData[1];
			String nodeName = testCaseData[2];
			String tagNodeName = testCaseData[3];
			String valueNodeName = testCaseData[4];
			builder.add(Arguments.arguments(tag, value, nodeName, tagNodeName, valueNodeName));
		}

		return builder.build();
	}

	/*
	 * Null arguments
	 */
	@Test
	public void testNullTag() {
		assertThrows(NullPointerException.class, () -> new HeaderTag(null, "somevalue", "A", "B", "C"));
	}

	@Test
	public void testNullValue() {
		assertThrows(NullPointerException.class, () -> new HeaderTag("123", null, "A", "B", "C"));
	}

	@Test
	public void testNull() {
		assertThrows(NullPointerException.class, () -> new HeaderTag(null, null, "A", "B", "C"));
	}

	/*
	 * Empty arguments
	 */
	@Test
	public void testEmptyTag() {
		assertThrows(IllegalArgumentException.class, () -> new HeaderTag("", "somevalue", "A", "B", "C"));
	}

	@Test
	public void testEmptyNodeName() {
		assertThrows(IllegalArgumentException.class, () -> new HeaderTag("t", "somevalue", "", "B", "C"));
	}

	@Test
	public void testEmptytagNodeName() {
		assertThrows(IllegalArgumentException.class, () -> new HeaderTag("t", "somevalue", "A", "", "C"));
	}

	@Test
	public void testEmptyValueNodeName() {
		new HeaderTag("t", "somevalue", "A", "B", "");
	}

	@Test
	public void testEmptyValue() {
		new HeaderTag("123", "", "A", "B", "C");
	}

	@Test
	public void testEmpty() {
		assertThrows(IllegalArgumentException.class, () -> new HeaderTag("", "", "A", "B", "C"));
	}

}
