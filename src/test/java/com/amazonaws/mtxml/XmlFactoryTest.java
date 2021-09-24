package com.amazonaws.mtxml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.amazonaws.mtxml.utils.XmlFactory;

class XmlFactoryTest {

	@Test
	void testGetDefaultDeclaration() {
		String testDeclaration = XmlFactory.getDeclaration();
		String ctrlDeclaration = String.format("<?xml version=\"%s\" encoding=\"%s\" ?>",
				XmlFactory.DEFAULT_XML_VERSION, XmlFactory.DEFAULT_XML_ENCODING);
		assertEquals(testDeclaration, ctrlDeclaration);
	}

	@ParameterizedTest
	@MethodSource("XmlDeclarationParameters")
	void testGetDeclaration(String version, String encoding) {
		String testDeclaration = XmlFactory.getDeclaration(version, encoding);
		String ctrlDeclaration = String.format("<?xml version=\"%s\" encoding=\"%s\" ?>", version, encoding);
		assertEquals(testDeclaration, ctrlDeclaration);
	}

	private static Stream<Arguments> XmlDeclarationParameters() {
		Stream.Builder<Arguments> sb = Stream.builder();
		sb.add(Arguments.arguments("1.0", "UTF-8"));
		sb.add(Arguments.arguments("1.0", "UTF-16"));
		sb.add(Arguments.arguments("1.0", "ISO-10646-UCS-2"));
		sb.add(Arguments.arguments("1.0", "ISO-10646-UCS-4"));
		sb.add(Arguments.arguments("1.0", "EUC-JP"));
		return sb.build();
	}

	@ParameterizedTest
	@ValueSource(strings = { "Test", "123", "mustbe&escaped", "thisal>so" })
	void testOpenNodeString(String nodeName) {
		String test = XmlFactory.openNode(nodeName);
		String ctrl = "<" + XmlFactory.xmlEscape(nodeName) + ">";
		assertEquals(test, ctrl);
	}

	@Test
	void testOpenNodeMultipleAttributes() {
		String nodeName = "MyNode";
		String[][] attributes = { { "att1", "123" }, { "numbertwo", "!XXXkjfaow" } };
		String test = XmlFactory.openNode(nodeName, attributes);
		String ctrl = String.format("<%s %s=\"%s\" %s=\"%s\">", XmlFactory.xmlEscape(nodeName),
				XmlFactory.xmlEscape(attributes[0][0]), XmlFactory.xmlEscape(attributes[0][1]),
				XmlFactory.xmlEscape(attributes[1][0]), XmlFactory.xmlEscape(attributes[1][1]));
		assertEquals(test, ctrl);
	}

	@Test
	void testOpenNodeSingleAttribute() {
		String nodeName = "RootNode";
		String attributeName = "My_attribute";
		String attributeValue = "Very big value!";
		String test = XmlFactory.openNode(nodeName, attributeName, attributeValue);
		String ctrl = String.format("<%s %s=\"%s\">", XmlFactory.xmlEscape(nodeName),
				XmlFactory.xmlEscape(attributeName), XmlFactory.xmlEscape(attributeValue));
		assertEquals(test, ctrl);
	}

	@ParameterizedTest
	@MethodSource("XMLEscapes")
	void testXmlEscape(String input, String expected) {
		assertEquals(XmlFactory.xmlEscape(input), expected);
	}

	private static Stream<Arguments> XMLEscapes() {
		Stream.Builder<Arguments> sb = Stream.builder();
		sb.add(Arguments.arguments("ASD", "ASD"));
		sb.add(Arguments.arguments("someAmper&Sans", "someAmper&amp;Sans"));
		sb.add(Arguments.arguments("<<Heio", "&lt;&lt;Heio"));
		sb.add(Arguments.arguments("A>B>C", "A&gt;B&gt;C"));
		sb.add(Arguments.arguments("\"Famous quote\"", "&quot;Famous quote&quot;"));
		sb.add(Arguments.arguments("Lots of 'apostrofes'", "Lots of &apos;apostrofes&apos;"));
		sb.add(Arguments.arguments("'&>", "&apos;&amp;&gt;"));
		return sb.build();
	}

	@ParameterizedTest
	@ValueSource(strings = { "Test", "123", "mustbe&escaped", "thisal>so" })
	void testCloseNode(String nodename) {
		String test = XmlFactory.closeNode(nodename);
		String ctrl = "</" + XmlFactory.xmlEscape(nodename) + ">";
		assertEquals(test, ctrl);
	}

	@ParameterizedTest
	@MethodSource("NodeTestsSingleAttribute")
	void testWriteNode(String nodeName, String nodeContent) {
		String ctrl = String.format("<%s>%s</%s>", XmlFactory.xmlEscape(nodeName), nodeContent,
				XmlFactory.xmlEscape(nodeName));
		String test = XmlFactory.writeNode(nodeName, nodeContent);
		assertEquals(test, ctrl);
	}

	@ParameterizedTest
	@MethodSource("NodeTestsSingleAttribute")
	void testWriteNodeMultipleAttributes(String nodeName, String nodeContent, String attributeName,
			String attributeValue) {

		String ctrlOpening = String.format("<%s %s=\"%s\" %s=\"%s\">", XmlFactory.xmlEscape(nodeName),
				XmlFactory.xmlEscape(attributeName), XmlFactory.xmlEscape(attributeValue),
				XmlFactory.xmlEscape(attributeName), XmlFactory.xmlEscape(attributeValue));
		String ctrlContent = nodeContent;
		String ctrlClosing = "</" + XmlFactory.xmlEscape(nodeName) + ">";
		String ctrl = ctrlOpening + ctrlContent + ctrlClosing;

		// To simplify we repeat the single attribute twice
		String[][] attributes = { { attributeName, attributeValue }, { attributeName, attributeValue } };
		String test = XmlFactory.writeNode(nodeName, nodeContent, attributes);
		assertEquals(test, ctrl);
	}

	@ParameterizedTest
	@MethodSource("NodeTestsSingleAttribute")
	void testWriteNodeOneAttribute(String nodeName, String nodeContent, String attributeName, String attributeValue) {
		String ctrlOpening = String.format("<%s %s=\"%s\">", XmlFactory.xmlEscape(nodeName),
				XmlFactory.xmlEscape(attributeName), XmlFactory.xmlEscape(attributeValue));
		String ctrlContent = nodeContent;
		String ctrlClosing = "</" + XmlFactory.xmlEscape(nodeName) + ">";
		String ctrl = ctrlOpening + ctrlContent + ctrlClosing;
		String test = XmlFactory.writeNode(nodeName, nodeContent, attributeName, attributeValue);
		assertEquals(test, ctrl);
	}

	private static Stream<Arguments> NodeTestsSingleAttribute() {
		Stream.Builder<Arguments> sb = Stream.builder();
		sb.add(Arguments.arguments("MyNode", "<SomeXml>Content</SomeXml>", "AttributeName", "AttributeValue"));
		sb.add(Arguments.arguments("MyMultilineNode", "<SomeXml><Line>Textcontent<Line><Line/></SomeXml>", "A&B&C",
				"1 < 10"));
		sb.add(Arguments.arguments("TextNode", "blablabla", "XXX", "YYY"));
		return sb.build();
	}

	@Test
	void testThrowsNullNodeNames() {
		assertThrows(NullPointerException.class, () -> XmlFactory.writeNode(null, "foobar"));
		assertThrows(NullPointerException.class, () -> XmlFactory.writeNode(null, "foobar", "foo", "bar"));
		assertThrows(NullPointerException.class, () -> XmlFactory.writeNode(null, "foobar", null));

		assertThrows(NullPointerException.class, () -> XmlFactory.closeNode(null));

		assertThrows(NullPointerException.class, () -> XmlFactory.openNode(null));
		assertThrows(NullPointerException.class, () -> XmlFactory.openNode(null, "foo", "bar"));
		assertThrows(NullPointerException.class, () -> XmlFactory.openNode(null, null));
	}

	@Test
	void testThrowsZeroLengthNodeNames() {
		assertThrows(IllegalArgumentException.class, () -> XmlFactory.writeNode("", "foobar"));
		assertThrows(IllegalArgumentException.class, () -> XmlFactory.writeNode("", "foobar", "foo", "bar"));
		assertThrows(IllegalArgumentException.class, () -> XmlFactory.writeNode("", "foobar", null));

		assertThrows(IllegalArgumentException.class, () -> XmlFactory.closeNode(""));

		assertThrows(IllegalArgumentException.class, () -> XmlFactory.openNode(""));
		assertThrows(IllegalArgumentException.class, () -> XmlFactory.openNode("", "foo", "bar"));
		assertThrows(IllegalArgumentException.class, () -> XmlFactory.openNode("", null));
	}

	@Test
	void testThrowsNullGetDeclaration() {
		assertThrows(NullPointerException.class, () -> XmlFactory.getDeclaration(null, null));
		assertThrows(NullPointerException.class, () -> XmlFactory.getDeclaration("foo", null));
		assertThrows(NullPointerException.class, () -> XmlFactory.getDeclaration(null, "bar"));
	}
}
