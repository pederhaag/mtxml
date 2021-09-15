package com.amazonaws.mtxml;

public class XmlFactory {
	private static final String[][] EMPTY_ARRAY = new String[0][0];
	private static final String XML_DECLARATION_TEMPLATE = "<?xml version=\"%s\" encoding=\"%s\" ?>";
	private static final String DEFAULT_XML_VERSION = "1.0";
	private static final String DEFAULT_XML_ENCODING = "UTF-8";

	static String getDeclaration() {
		return getDeclaration(DEFAULT_XML_VERSION, DEFAULT_XML_ENCODING);
	}

	static String getDeclaration(String version, String encoding) {
		return String.format(XML_DECLARATION_TEMPLATE, version, encoding);
	}

	static String openNode(String nodeName) {
		return "<" + nodeName + ">";

	}

	static String openNode(String nodeName, String[][] attributes) {
		String attributeString = "";

		for (int i = 0; i < attributes.length; i++) {
			attributeString += String.format(" %s=\"%s\"", attributes[i][0], attributes[i][1]);
		}
		return String.format("<%s%s>", nodeName, attributeString);

	}

	static String openNode(String nodeName, String attributeName, String attributeValue) {
		String[][] attributes = { new String[] { attributeName, attributeValue } };
		return openNode(nodeName, attributes);

	}

	static String closeNode(String nodeName) {
		return "</" + nodeName + ">";
	}

	static String writeNode(String nodeName, String nodeContent, String attributeName, String attributeValue) {
		return String.format("%s%s%s", openNode(nodeName, attributeName, attributeValue), nodeContent,
				closeNode(nodeName));
	}

	static String writeNode(String nodeName, String nodeContent, String[][] attributes) {
		return String.format("%s%s%s", openNode(nodeName, attributes), nodeContent, closeNode(nodeName));
	}

	static String writeNode(String nodeName, String nodeContent) {
		return String.format("%s%s%s", openNode(nodeName), nodeContent, closeNode(nodeName));
	}

	public static void main(String[] args) {

		System.out.println(writeNode("Tag123", "NOK123333"));
		System.out.println(writeNode("Tag123", "NOK123333", "Qualifier", "SETT"));
		System.out.println(getDeclaration());
	}
}
