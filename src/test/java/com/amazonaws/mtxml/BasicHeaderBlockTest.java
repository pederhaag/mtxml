package com.amazonaws.mtxml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.amazonaws.test.utils.TestCases;
import com.amazonaws.test.utils.TestingUtils;

public class BasicHeaderBlockTest {
	private static ArrayList<Map<String, String>> validBlocks;
	private static String[] invalidBlocks;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		validBlocks = TestCases.getValidBasicHeaderBlockTestCases();
		invalidBlocks = TestCases.getInvalidBasicHeaderBlockTestCases();
	}

	private static String createAndGet(String blockContents, String field) {
		return new BasicHeaderBlock(blockContents).getData(field);
	}

	/*
	 * Valid constructors
	 */
	@ParameterizedTest
	@MethodSource("validBlocks")
	void testBasicHeaderBlockValid(Map<String, String> blockData) {
		new BasicHeaderBlock(blockData.get("rawContent"));
	}

	/*
	 * Invalid constructors
	 */
	@ParameterizedTest
	@MethodSource("invalidBlocks")
	void testBasicHeaderBlockInvalid(String input) {
		assertThrows(MTSyntaxException.class, () -> new BasicHeaderBlock(input));
	}

	/*
	 * Invalid (null) constructors
	 */
	@Test
	void testBasicHeaderBlockInvalidNull() {
		assertThrows(NullPointerException.class, () -> new BasicHeaderBlock(null));
	}

	/**
	 * To XML
	 */
	@ParameterizedTest
	@MethodSource("validBlocks")
	void testToXml(Map<String, String> blockData) {
		String ctrlXml = blockData.get("xml");
		String testXml = new BasicHeaderBlock(blockData.get("rawContent")).toXml();
		TestingUtils.assertXMLEqual(testXml, ctrlXml);
	}

	/*
	 * AppID
	 */
	@ParameterizedTest
	@MethodSource("validBlocks")
	void testGetAppID(Map<String, String> blockData) {
		mapAssertEqual(blockData, "ApplicationIdentifier");
	}

	/*
	 * ServiceID
	 */
	@ParameterizedTest
	@MethodSource("validBlocks")
	void testGetServiceIdentifier(Map<String, String> blockData) {
		mapAssertEqual(blockData, "ServiceIdentifier");
	}

	/*
	 * LTAddress
	 */
	@ParameterizedTest
	@MethodSource("validBlocks")
	void testGetLTAddress(Map<String, String> blockData) {
		mapAssertEqual(blockData, "LTAddress");
	}

	/*
	 * SessionNumber
	 */
	@ParameterizedTest
	@MethodSource("validBlocks")
	void testGetSessionNumber(Map<String, String> blockData) {
		mapAssertEqual(blockData, "SessionNumber");
	}

	/*
	 * SequenceNumber
	 */
	@ParameterizedTest
	@MethodSource("validBlocks")
	void testGetSequenceNumber(Map<String, String> blockData) {
		mapAssertEqual(blockData, "SequenceNumber");
	}

	/*
	 * Invalid fields
	 */
	@ParameterizedTest
	@ValueSource(strings = { "blabal", "", })
	void testInvalidField(String input) {
		assertThrows(IllegalArgumentException.class, () -> new BasicHeaderBlock(input));
	}

	/*
	 * Invalid (null) fields
	 */
	@Test
	void testNullField() {
		assertThrows(NullPointerException.class, () -> new BasicHeaderBlock(null));
	}

	private static Stream<Map<String, String>> validBlocks() {
		return validBlocks.stream();
	}

	private static Stream<String> invalidBlocks() {
		return Stream.of(invalidBlocks);
	}

	private static void mapAssertEqual(Map<String, String> blockData, String fieldName) {
		assertEquals(createAndGet(blockData.get("rawContent"), fieldName), blockData.get(fieldName));
	}

}
