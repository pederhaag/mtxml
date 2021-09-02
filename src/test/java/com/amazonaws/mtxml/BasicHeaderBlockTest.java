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

public class BasicHeaderBlockTest {

	private String createAndGet(String blockContents, String field) {
		return new BasicHeaderBlock(blockContents).getData(field);
	}

	/*
	 * Valid constructors
	 */
	@ParameterizedTest
	@ValueSource(strings = { "{1:F01MYBABBICAXXX0878450607}", "{1:F01MYBABBICAEGC0878450607}" })
	void testBasicHeaderBlockValid(String input) {
		new BasicHeaderBlock(input);
	}

	/*
	 * Invalid constructors
	 */
	@ParameterizedTest
	@ValueSource(strings = { "{2:F01MYBABBICAXXX0878450607}", "{1:F01MYBABBICAXXX08784506078}",
			"{1:F01MYBABBICAXXX08S8450607}" })
	void testBasicHeaderBlockInvalid(String input) {
		assertThrows(SyntaxException.class, () -> new BasicHeaderBlock(input));
	}

	/*
	 * Invalid (null) constructors
	 */
	@Test
	public void testBasicHeaderBlockInvalidNull() {
		assertThrows(NullPointerException.class, () -> new BasicHeaderBlock(null));
	}

	@Test
	public void testToXml() {
		fail("Not yet implemented");
	}

	/*
	 * BlockIdentifier
	 */
	@ParameterizedTest
	@ValueSource(strings = { "{1:F01MYBABBICAXXX0878450607}", "{1:G01MYBABBICAVFR0878455555}" })
	void testGetBlockIdentifier(String input) {
		assertEquals(createAndGet(input, "BlockIdentifier"), "1");
	}

	/*
	 * AppID
	 */
	@ParameterizedTest
	@MethodSource("blockToAppID")
	void testGetAppID(String blockContents, String AppID) {
		assertEquals(createAndGet(blockContents, "AppID"), AppID);
	}

	private static Stream<Arguments> blockToAppID() {
		return Stream.of(Arguments.arguments("{1:F01MYBABBICAXXX0878450607}", "F"),
				Arguments.arguments("{1:T01MYBABBICAXXX0878450607}", "T"));
	}

	/*
	 * ServiceID
	 */
	@ParameterizedTest
	@MethodSource("blockToServiceID")
	void testGetServiceID(String blockContents, String ServiceID) {
		assertEquals(createAndGet(blockContents, "ServiceID"), ServiceID);
	}

	private static Stream<Arguments> blockToServiceID() {
		return Stream.of(Arguments.arguments("{1:F01MYBABBICAXXX0878450607}", "01"),
				Arguments.arguments("{1:T99MYBABBICAXXX0878450607}", "99"));
	}

	/*
	 * LTAdress
	 */
	@ParameterizedTest
	@MethodSource("blockToLTAdress")
	void testGetLTAdress(String blockContents, String LTAdress) {
		assertEquals(createAndGet(blockContents, "LTAdress"), LTAdress);
	}

	private static Stream<Arguments> blockToLTAdress() {
		return Stream.of(Arguments.arguments("{1:F01MYBABBICAXXX0878450607}", "MYBABBICAXXX"),
				Arguments.arguments("{1:F01MYSSSBICASEK0878450607}", "MYSSSBICASEK"));
	}

	/*
	 * SessionNumber
	 */
	@ParameterizedTest
	@MethodSource("blockToSessionNumber")
	void testGetSessionNumber(String blockContents, String sessNo) {
		assertEquals(createAndGet(blockContents, "SessionNumber"), sessNo);
	}

	private static Stream<Arguments> blockToSessionNumber() {
		return Stream.of(Arguments.arguments("{1:F01MYBABBICAXXX0878450607}", "0878"),
				Arguments.arguments("{1:F01MYSSSBICASEK0999950607}", "0999"));
	}

	/*
	 * SequenceNumber
	 */
	@ParameterizedTest
	@MethodSource("blockToSequenceNumber")
	void testGetSequenceNumber(String blockContents, String seqNo) {
		assertEquals(createAndGet(blockContents, "SequenceNumber"), seqNo);
	}

	private static Stream<Arguments> blockToSequenceNumber() {
		return Stream.of(Arguments.arguments("{1:F01MYBABBICAXXX0878450607}", "450607"),
				Arguments.arguments("{1:F01MYSSSBICASEK0999950607}", "950607"));
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
	public void testNullField() {
		assertThrows(NullPointerException.class, () -> new BasicHeaderBlock(null));
	}

	/*
	 * Raw Data
	 */
	@ParameterizedTest
	@ValueSource(strings = { "{1:F01MYBABBICAXXX0878450607}", "{1:F01MYSSSBICASEK0999950607}", })
	void testGetRawContent(String input) {
		assertEquals(createAndGet(input, "RawData"), input);
	}

}
