package com.amazonaws.mtxml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.amazonaws.mtxml.utils.XmlFactory;
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
	 * BlockIdentifier
	 */
	@ParameterizedTest
	@MethodSource("validBlocks")
	void testGetBlockIdentifier(Map<String, String> blockData) {
		mapAssertEqual(blockData, "BlockIdentifier");
	}

	/*
	 * InOutID
	 */
	@ParameterizedTest
	@MethodSource("validBlocks")
	void testGetInOutID(Map<String, String> blockData) {
		mapAssertEqual(blockData, "InOutID");
	}

	/*
	 * MT
	 */
	@ParameterizedTest
	@MethodSource("validBlocks")
	void testGetMT(Map<String, String> blockData) {
		mapAssertEqual(blockData, "MT");
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
		mapAssertEqual(blockData, "MIR.SequenceNumber");
	}

	/*
	 * MIR.SendersDate
	 */
	@ParameterizedTest
	@MethodSource("validOutputBlocks")
	void testGetMIRSendersDate(Map<String, String> blockData) {
		mapAssertEqual(blockData, "MIR.SendersDate");
	}

	/*
	 * MIR.LogicalTerminal
	 */
	@ParameterizedTest
	@MethodSource("validOutputBlocks")
	void testGetMIRLogicalTerminal(Map<String, String> blockData) {
		mapAssertEqual(blockData, "MIR.LogicalTerminal");
	}

	/*
	 * MIR.SessionNumber
	 */
	@ParameterizedTest
	@MethodSource("validOutputBlocks")
	void testGetMIRSessionNumber(Map<String, String> blockData) {
		mapAssertEqual(blockData, "MIR.SessionNumber");
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

	private static String expectedXml(Map<String, String> blockData) throws Exception {
		StringBuilder sb = new StringBuilder();

		sb.append(XmlFactory.openNode("ApplicationHeader"));
		sb.append(XmlFactory.writeNode("InputOutputIdentifier", blockData.get("InOutID")));
		sb.append(XmlFactory.writeNode("MessageType", blockData.get("MT")));

		if (blockData.get("InOutID").equals("I")) {
			sb.append(XmlFactory.writeNode("DestAddress", blockData.get("DestAddress")));

			sb.append(XmlFactory.openNode("DestAddress_Details"));
			sb.append(XmlFactory.writeNode("BIC", blockData.get("DestAddress.BIC")));
			sb.append(XmlFactory.writeNode("LogicalTerminal", blockData.get("DestAddress.LogicalTerminal")));
			sb.append(XmlFactory.writeNode("BIC8", blockData.get("DestAddress.BIC8")));
			sb.append(XmlFactory.closeNode("DestAddress_Details"));

			sb.append(XmlFactory.writeNode("Priority", blockData.get("Priority")));
			sb.append(XmlFactory.writeNode("DeliveryMonitoring", blockData.get("DeliveryMonitoring")));
			sb.append(XmlFactory.writeNode("ObsolencePeriod", blockData.get("ObsolencePeriod")));

		} else if (blockData.get("InOutID").equals("O")) {
			sb.append(XmlFactory.writeNode("InputTime", blockData.get("InputTime")));
			sb.append(XmlFactory.writeNode("MIR", blockData.get("MIR")));

			sb.append(XmlFactory.openNode("MIR_Details"));
			sb.append(XmlFactory.writeNode("SendersDate", blockData.get("MIR.SendersDate")));
			sb.append(XmlFactory.writeNode("LogicalTerminal", blockData.get("MIR.LogicalTerminal")));
			sb.append(XmlFactory.writeNode("SessionNumber", blockData.get("MIR.SessionNumber")));
			sb.append(XmlFactory.writeNode("SequenceNumber", blockData.get("MIR.SequenceNumber")));
			sb.append(XmlFactory.closeNode("MIR_Details"));

			sb.append(XmlFactory.writeNode("OutputDate", blockData.get("OutputDate")));
			sb.append(XmlFactory.writeNode("OutputTime", blockData.get("OutputTime")));
			sb.append(XmlFactory.writeNode("Priority", blockData.get("Priority")));
		} else
			throw new Exception("Unable to identify InOutID when constructing expected XML");

		sb.append(XmlFactory.closeNode("ApplicationHeader"));

		return sb.toString();
	}

}
