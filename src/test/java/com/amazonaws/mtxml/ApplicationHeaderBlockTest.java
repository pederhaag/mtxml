package com.amazonaws.mtxml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class ApplicationHeaderBlockTest {
	final static String outputBlock1 = "{2:O9400144210831BANKBICSAXXX61563916672108310144N}";
	final static String outputBlock2 = "{2:O5640004210831MYBANKTUBBRA99001234560101012000N}";

	final static String invalidOutputBlock1 = "{3:O9400144210831BANKBICSAXXX61563916672108310144N}";
	final static String invalidOutputBlock2 = "{2:O5640004210831MYBANKTUBBRA99001234560101012000}";

	final static String inputBlock1 = "{2:I527ABCDEFGHXBRAN3}";
	final static String inputBlock2 = "{2:I299DDDDEFGHXXXXU007}";

	final static String invalidInputBlock1 = "{2:I527ABCDEFGHXBR?N}";
	final static String invalidInputBlock2 = "{2:I99DDDDEFGHXXXXN}";

	private String createAndGet(String blockContents, String field) {
		return new ApplicationHeaderBlock(blockContents).getData(field);
	}

	/*
	 * Valid constructors
	 */
	@ParameterizedTest
	@ValueSource(strings = { outputBlock1, outputBlock2, inputBlock1, inputBlock2 })
	void testApplicationHeaderBlock(String input) {
		new ApplicationHeaderBlock(input);
	}

	/*
	 * Invalid constructors
	 */
	@ParameterizedTest
	@ValueSource(strings = { invalidOutputBlock1, invalidOutputBlock2, invalidInputBlock1, invalidInputBlock2 })
	void testApplicationHeaderBlockInvalid(String input) {
		assertThrows(SyntaxException.class, () -> new ApplicationHeaderBlock(input));
	}

	/*
	 * Invalid (null) constructors
	 */
	@Test
	void testApplicationHeaderBlockInvalidNull() {
		assertThrows(NullPointerException.class, () -> new ApplicationHeaderBlock(null));
	}

	/*
	 * BlockIdentifier
	 */
	@ParameterizedTest
	@ValueSource(strings = { outputBlock1, outputBlock2, inputBlock1, inputBlock2 })
	void testGetBlockIdentifier(String input) {
		assertEquals(createAndGet(input, "BlockIdentifier"), "2");
	}

	/*
	 * InOutID
	 */
	@ParameterizedTest
	@MethodSource("blockToInOutID")
	void testGetInOutID(String blockContents, String InOutID) {
		assertEquals(createAndGet(blockContents, "InOutID"), InOutID);
	}

	private static Stream<Arguments> blockToInOutID() {
		return Stream.of(Arguments.arguments(outputBlock1, "O"), Arguments.arguments(outputBlock2, "O"),
				Arguments.arguments(inputBlock1, "I"), Arguments.arguments(inputBlock2, "I"));
	}

	/*
	 * MT
	 */
	@ParameterizedTest
	@MethodSource("blockToMT")
	void testGetMT(String blockContents, String mt) {
		assertEquals(createAndGet(blockContents, "MT"), mt);
	}

	private static Stream<Arguments> blockToMT() {
		return Stream.of(Arguments.arguments(outputBlock1, "940"), Arguments.arguments(outputBlock2, "564"),
				Arguments.arguments(inputBlock1, "527"), Arguments.arguments(inputBlock2, "299"));
	}

	/*
	 * DestAddress
	 */
	@ParameterizedTest
	@MethodSource("blockToDestAddress")
	void testGetDestAddress(String blockContents, String destAddress) {
		assertEquals(createAndGet(blockContents, "DestAddress"), destAddress);
	}

	private static Stream<Arguments> blockToDestAddress() {
		return Stream.of(Arguments.arguments(outputBlock1, null), Arguments.arguments(outputBlock2, null),
				Arguments.arguments(inputBlock1, "ABCDEFGHXBRA"), Arguments.arguments(inputBlock2, "DDDDEFGHXXXX"));
	}

	/*
	 * Priority
	 */
	@ParameterizedTest
	@MethodSource("blockToPriority")
	void testGetPriority(String blockContents, String priority) {
		assertEquals(createAndGet(blockContents, "Priority"), priority);
	}

	private static Stream<Arguments> blockToPriority() {
		return Stream.of(Arguments.arguments(outputBlock1, null), Arguments.arguments(outputBlock2, null),
				Arguments.arguments(inputBlock1, "N"), Arguments.arguments(inputBlock2, "U"));
	}

	/*
	 * DeliveryMonitoring
	 */
	@ParameterizedTest
	@MethodSource("blockToDelMonitoring")
	void testGetDelMonitoring(String blockContents, String delMonitoring) {
		assertEquals(createAndGet(blockContents, "DeliveryMonitoring"), delMonitoring);
	}

	private static Stream<Arguments> blockToDelMonitoring() {
		return Stream.of(Arguments.arguments(outputBlock1, null), Arguments.arguments(outputBlock2, null),
				Arguments.arguments(inputBlock1, "3"), Arguments.arguments(inputBlock2, null));
	}

	/*
	 * ObsolencePeriod
	 */
	@ParameterizedTest
	@MethodSource("blockToObsPeriod")
	void testGetObsPeriod(String blockContents, String obsPeriod) {
		assertEquals(createAndGet(blockContents, "ObsolencePeriod"), obsPeriod);
	}

	private static Stream<Arguments> blockToObsPeriod() {
		return Stream.of(Arguments.arguments(outputBlock1, null), Arguments.arguments(outputBlock2, null),
				Arguments.arguments(inputBlock1, null), Arguments.arguments(inputBlock2, "007"));
	}

	/*
	 * InputTime
	 */
	@ParameterizedTest
	@MethodSource("blockToInputTime")
	void testGetInputTime(String blockContents, String inputTime) {
		assertEquals(createAndGet(blockContents, "InputTime"), inputTime);
	}

	private static Stream<Arguments> blockToInputTime() {
		return Stream.of(Arguments.arguments(outputBlock1, "0144"), Arguments.arguments(outputBlock2, "0004"),
				Arguments.arguments(inputBlock1, null), Arguments.arguments(inputBlock2, null));
	}

	/*
	 * OutputDate
	 */
	@ParameterizedTest
	@MethodSource("blockToOutputDate")
	void testGetOutputDate(String blockContents, String outputDate) {
		assertEquals(createAndGet(blockContents, "OutputDate"), outputDate);
	}

	private static Stream<Arguments> blockToOutputDate() {
		return Stream.of(Arguments.arguments(outputBlock1, "210831"), Arguments.arguments(outputBlock2, "010101"),
				Arguments.arguments(inputBlock1, null), Arguments.arguments(inputBlock2, null));
	}

	/*
	 * OutputDate
	 */
	@ParameterizedTest
	@MethodSource("blockToOutputTime")
	void testGetOutputTime(String blockContents, String outputTime) {
		assertEquals(createAndGet(blockContents, "OutputTime"), outputTime);
	}

	private static Stream<Arguments> blockToOutputTime() {
		return Stream.of(Arguments.arguments(outputBlock1, "0144"), Arguments.arguments(outputBlock2, "2000"),
				Arguments.arguments(inputBlock1, null), Arguments.arguments(inputBlock2, null));
	}

	/*
	 * MIR
	 */
	@ParameterizedTest
	@MethodSource("blockToMIR")
	void testGetMIR(String blockContents, String MIR) {
		assertEquals(createAndGet(blockContents, "MIR"), MIR);
	}

	private static Stream<Arguments> blockToMIR() {
		String outputBlock1MIR = "210831BANKBICSAXXX6156391667";
		String outputBlock2MIR = "210831MYBANKTUBBRA9900123456";
		return Stream.of(Arguments.arguments(outputBlock1, outputBlock1MIR),
				Arguments.arguments(outputBlock2, outputBlock2MIR), Arguments.arguments(inputBlock1, null),
				Arguments.arguments(inputBlock2, null));
	}

	/*
	 * MIR.SendersDate
	 */
	@ParameterizedTest
	@MethodSource("blockToMIRSendersDate")
	void testGetMIRSendersDate(String blockContents, String MIRSendersDate) {
		assertEquals(createAndGet(blockContents, "MIR.SendersDate"), MIRSendersDate);
	}

	private static Stream<Arguments> blockToMIRSendersDate() {
		return Stream.of(Arguments.arguments(outputBlock1, "210831"), Arguments.arguments(outputBlock2, "210831"),
				Arguments.arguments(inputBlock1, null), Arguments.arguments(inputBlock2, null));
	}

	/*
	 * MIR.LogicalTerminal
	 */
	@ParameterizedTest
	@MethodSource("blockToMIRLogicalTerminal")
	void testGetMIRLogicalTerminal(String blockContents, String lt) {
		assertEquals(createAndGet(blockContents, "MIR.LogicalTerminal"), lt);
	}

	private static Stream<Arguments> blockToMIRLogicalTerminal() {
		return Stream.of(Arguments.arguments(outputBlock1, "BANKBICSAXXX"),
				Arguments.arguments(outputBlock2, "MYBANKTUBBRA"), Arguments.arguments(inputBlock1, null),
				Arguments.arguments(inputBlock2, null));
	}

	/*
	 * MIR.SessionNumber
	 */
	@ParameterizedTest
	@MethodSource("blockToMIRSessionNumber")
	void testGetMIRSessionNumber(String blockContents, String sessionNo) {
		assertEquals(createAndGet(blockContents, "MIR.SessionNumber"), sessionNo);
	}

	private static Stream<Arguments> blockToMIRSessionNumber() {
		return Stream.of(Arguments.arguments(outputBlock1, "6156"), Arguments.arguments(outputBlock2, "9900"),
				Arguments.arguments(inputBlock1, null), Arguments.arguments(inputBlock2, null));
	}

	/*
	 * MIR.SequenceNumber
	 */
	@ParameterizedTest
	@MethodSource("blockToMIRSequenceNumber")
	void testGetMIRSequenceNumber(String blockContents, String seqNo) {
		assertEquals(createAndGet(blockContents, "MIR.SequenceNumber"), seqNo);
	}

	private static Stream<Arguments> blockToMIRSequenceNumber() {
		return Stream.of(Arguments.arguments(outputBlock1, "391667"), Arguments.arguments(outputBlock2, "123456"),
				Arguments.arguments(inputBlock1, null), Arguments.arguments(inputBlock2, null));
	}

	@Test
	public void testToXml() {
		fail("Not yet implemented");
	}

}
