package com.amazonaws.mtxml;

/**
 * 
 * A Factory providing an API for writing XML-syntax
 *
 */
public class XmlFactory {
	/**
	 * A String.format-template for a XML-declaration
	 */
	private static final String XML_DECLARATION_TEMPLATE = "<?xml version=\"%s\" encoding=\"%s\" ?>";

	/**
	 * Default XML version used in a XML-declaration
	 */
	private static final String DEFAULT_XML_VERSION = "1.0";

	/**
	 * Default encoding used in a XML-declaration
	 */
	private static final String DEFAULT_XML_ENCODING = "UTF-8";

	/**
	 * Get default XML Declaration string
	 */
	static String getDeclaration() {
		return getDeclaration(DEFAULT_XML_VERSION, DEFAULT_XML_ENCODING);
	}

	/**
	 * Get XML Declaration string with specified XML version and encoding
	 */
	static String getDeclaration(String version, String encoding) {
		return String.format(XML_DECLARATION_TEMPLATE, version, encoding);
	}

	/**
	 * Write opening tag of XML node
	 * 
	 * @param nodeName Name of the XML node
	 * @return opening tag of XML node, i.e. <i>&lt;nodeName&gt;</i>
	 */
	static String openNode(String nodeName) {
		return "<" + nodeName + ">";

	}

	/**
	 * Write opening tag of XML node with specified attributes
	 * 
	 * @param nodeName   Name of the XML node
	 * @param attributes {@code String[][]} array where each entry in first
	 *                   dimension is a two-dimensional {@code String}-array of the
	 *                   format {@code [attribute, attributevalue]}
	 * @return opening tag of XML node
	 */
	static String openNode(String nodeName, String[][] attributes) {
		String attributeString = "";

		for (int i = 0; i < attributes.length; i++) {
			attributeString += String.format(" %s=\"%s\"", attributes[i][0], attributes[i][1]);
		}
		return String.format("<%s%s>", nodeName, attributeString);

	}

	/**
	 * Write opening tag of XML node with a single specified attribute
	 * 
	 * @param nodeName       Name of the XML node
	 * @param attributeName  Attribute name
	 * @param attributeValue Attribute value
	 * @return opening tag of XML node
	 */
	static String openNode(String nodeName, String attributeName, String attributeValue) {
		String[][] attributes = { new String[] { attributeName, attributeValue } };
		return openNode(nodeName, attributes);

	}

	/**
	 * Write closing XML-tag for a node
	 */
	static String closeNode(String nodeName) {
		return "</" + nodeName + ">";
	}

	/**
	 * Write a full node with opening/closing tags and contents with a single
	 * attribute
	 * 
	 * @param nodeName       Name of the XML node
	 * @param nodeContent    Value of the node
	 * @param attributeName  Attribute name
	 * @param attributeValue Attribute value
	 * @return XML node with opening and closing tags, an attribute and contents
	 */
	static String writeNode(String nodeName, String nodeContent, String attributeName, String attributeValue) {
		return String.format("%s%s%s", openNode(nodeName, attributeName, attributeValue), nodeContent,
				closeNode(nodeName));
	}

	/**
	 * Write a full node with opening/closing tags and contents with attributes
	 * 
	 * @param nodeName    Name of the XML node
	 * @param nodeContent Value of the node
	 * @param attributes  {@code String[][]} array where each entry in first
	 *                    dimension is a two-dimensional {@code String}-array of the
	 *                    format {@code [attribute, attributevalue]}
	 * @return XML node with opening and closing tags, attributes and contents
	 */
	static String writeNode(String nodeName, String nodeContent, String[][] attributes) {
		return String.format("%s%s%s", openNode(nodeName, attributes), nodeContent, closeNode(nodeName));
	}

	/**
	 * Write a full node with opening/closing tags and contents without attributes
	 * 
	 * @param nodeName    Name of the XML node
	 * @param nodeContent Value of the node
	 * @return XML node with opening and closing tags and contents
	 */
	static String writeNode(String nodeName, String nodeContent) {
		return String.format("%s%s%s", openNode(nodeName), nodeContent, closeNode(nodeName));
	}

}
