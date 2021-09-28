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

import com.amazonaws.test.utils.TestCases;
import com.amazonaws.test.utils.TestingUtils;

public class ApplicationHeaderBlockTest {

	private static ArrayList<Map<String, String>> validInputBlocks;
	private static ArrayList<Map<String, String>> validOutputBlocks;
	private static ArrayList<Map<String, String>> validBlocks;
	private static String[] invalidBlocks;

	private static String createAndGet(String blockContents, String field) {
		return new ApplicationHeaderBlock(blockContents).getData(field);
	}

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		validInputBlocks = TestCases.getValidInputApplicationHeaderBlockTestCases();
		validOutputBlocks = TestCases.getValidOutputApplicationHeaderBlockTestCases();

		validBlocks = new ArrayList<Map<String, String>>();
		validBlocks.addAll(validInputBlocks);
		validBlocks.addAll(validOutputBlocks);

		invalidBlocks = TestCases.getInvalidApplicationHeaderBlockTestCases();
	}

	/*
	 * Valid constructors
	 */
	@ParameterizedTest
	@MethodSource("validBlocks")
	void testApplicationHeaderBlock(Map<String, String> blockData) {
		new ApplicationHeaderBlock(blockData.get("rawContent"));
	}

	/*
	 * Invalid constructors
	 */
	@ParameterizedTest
	@MethodSource("invalidBlocks")
	void testApplicationHeaderBlockInvalid(String input) {
		assertThrows(MTSyntaxException.class, () -> new ApplicationHeaderBlock(input));
	}

	/*
	 * Invalid (null) constructors
	 */
	@Test
	void testApplicationHeaderBlockInvalidNull() {
		assertThrows(NullPointerException.class, () -> new ApplicationHeaderBlock(null));
	}

	/*
	 * To XML: Input blocks
	 */
	@ParameterizedTest
	@MethodSource("validInputBlocks")
	void testToXmlInputBlocks(Map<String, String> blockData) {
		String ctrlXml = blockData.get("xml");
		String testXml = new ApplicationHeaderBlock(blockData.get("rawContent")).toXml();
		TestingUtils.assertXMLEqual(testXml, ctrlXml);
	}

	/*
	 * To XML: Output blocks
	 */
	@ParameterizedTest
	@MethodSource("validOutputBlocks")
	void testToXmlOutputBlocks(Map<String, String> blockData) {
		String ctrlXml = blockData.get("xml");
		String testXml = new ApplicationHeaderBlock(blockData.get("rawContent")).toXml();
		TestingUtils.assertXMLEqual(testXml, ctrlXml);
	}

	/*
	 * InOutID
	 */
	@ParameterizedTest
	@MethodSource("validBlocks")
	void testGetInputOutputIdentifier(Map<String, String> blockData) {
		mapAssertEqual(blockData, "InputOutputIdentifier");
	}

	/*
	 * MT
	 */
	@ParameterizedTest
	@MethodSource("validBlocks")
	void testGetMT(Map<String, String> blockData) {
		mapAssertEqual(blockData, "MessageType");
	}

	/*
	 * DestAddress
	 */
	@ParameterizedTest
	@MethodSource("validInputBlocks")
	void testGetDestAddress(Map<String, String> blockData) {
		mapAssertEqual(blockData, "DestAddress");
	}

	/*
	 * Priority
	 */
	@ParameterizedTest
	@MethodSource("validBlocks")
	void testGetPriority(Map<String, String> blockData) {
		mapAssertEqual(blockData, "Priority");
	}

	/*
	 * DeliveryMonitoring
	 */
	@ParameterizedTest
	@MethodSource("validInputBlocks")
	void testGetDelMonitoring(Map<String, String> blockData) {
		mapAssertEqual(blockData, "DeliveryMonitoring");
	}

	/*
	 * ObsolencePeriod
	 */
	@ParameterizedTest
	@MethodSource("validInputBlocks")
	void testGetObsPeriod(Map<String, String> blockData) {
		mapAssertEqual(blockData, "ObsolencePeriod");
	}

	/*
	 * InputTime
	 */
	@ParameterizedTest
	@MethodSource("validOutputBlocks")
	void testGetInputDate(Map<String, String> blockData) {
		mapAssertEqual(blockData, "InputTime");
	}

	/*
	 * OutputDate
	 */
	@ParameterizedTest
	@MethodSource("validOutputBlocks")
	void testGetOutputDate(Map<String, String> blockData) {
		mapAssertEqual(blockData, "OutputDate");
	}

	/*
	 * OutputTime
	 */
	@ParameterizedTest
	@MethodSource("validOutputBlocks")
	void testGetOutputTime(Map<String, String> blockData) {
		mapAssertEqual(blockData, "OutputTime");
	}

	/*
	 * MIR
	 */
	@ParameterizedTest
	@MethodSource("validOutputBlocks")
	void testGetMIR(Map<String, String> blockData) {
		mapAssertEqual(blockData, "MIR");
	}

	/*
	 * MIR.SequenceNumber
	 */
	@ParameterizedTest
	@MethodSource("validOutputBlocks")
	void testGetMIRSequenceNumber(Map<String, String> blockData) {
		mapAssertEqual(blockData, "MIR_Details.SequenceNumber");
	}

	/*
	 * MIR.SendersDate
	 */
	@ParameterizedTest
	@MethodSource("validOutputBlocks")
	void testGetMIRSendersDate(Map<String, String> blockData) {
		mapAssertEqual(blockData, "MIR_Details.SendersDate");
	}

	/*
	 * MIR.LogicalTerminal
	 */
	@ParameterizedTest
	@MethodSource("validOutputBlocks")
	void testGetMIRLogicalTerminal(Map<String, String> blockData) {
		mapAssertEqual(blockData, "MIR_Details.LogicalTerminal");
	}

	/*
	 * MIR.SessionNumber
	 */
	@ParameterizedTest
	@MethodSource("validOutputBlocks")
	void testGetMIRSessionNumber(Map<String, String> blockData) {
		mapAssertEqual(blockData, "MIR_Details.SessionNumber");
	}

	private static Stream<Map<String, String>> validOutputBlocks() {
		return validOutputBlocks.stream();
	}

	private static Stream<Map<String, String>> validInputBlocks() {
		return validInputBlocks.stream();
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
