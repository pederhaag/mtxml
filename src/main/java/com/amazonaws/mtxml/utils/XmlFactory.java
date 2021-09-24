package com.amazonaws.mtxml.utils;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Objects;

import javax.xml.XMLConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

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
	public static final String DEFAULT_XML_VERSION = "1.0";

	/**
	 * Default encoding used in a XML-declaration
	 */
	public static final String DEFAULT_XML_ENCODING = "UTF-8";

	/**
	 * Get default XML Declaration string
	 */
	public static String getDeclaration() {
		return getDeclaration(DEFAULT_XML_VERSION, DEFAULT_XML_ENCODING);
	}

	/**
	 * Get XML Declaration string with specified XML version and encoding
	 */
	public static String getDeclaration(String version, String encoding) {
		checkNullAndLength(version, "XML version");
		checkNullAndLength(encoding, "encoding");
		return String.format(XML_DECLARATION_TEMPLATE, xmlEscape(version), xmlEscape(encoding));
	}

	/**
	 * Write opening tag of XML node
	 * 
	 * @param nodeName Name of the XML node
	 * @return opening tag of XML node, i.e. <i>&lt;nodeName&gt;</i>
	 */
	public static String openNode(String nodeName) {
		checkNullAndLength(nodeName, "Node name");
		return "<" + xmlEscape(nodeName) + ">";

	}

	/**
	 * Escape XML special characters
	 * 
	 * @param input original text to sanitize
	 * @return Original text where XML characters are escaped
	 */
	public static String xmlEscape(String input) {
		Objects.requireNonNull(input);
		return input.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\"", "&quot;")
				.replaceAll("'", "&apos;");
	}

	public static String prettify(String input, int indent, boolean omitDeclaration) {
		try {
			Source xmlInput = new StreamSource(new StringReader(input));
			StringWriter stringWriter = new StringWriter();
			StreamResult xmlOutput = new StreamResult(stringWriter);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformerFactory.setAttribute("indent-number", indent);
			transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
			Transformer transformer = transformerFactory.newTransformer();
			if (omitDeclaration)
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(xmlInput, xmlOutput);
			return xmlOutput.getWriter().toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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
	public static String openNode(String nodeName, String[][] attributes) {
		checkNullAndLength(nodeName, "Node name");
		String attributeString = "";

		for (int i = 0; i < attributes.length; i++) {
			attributeString += String.format(" %s=\"%s\"", xmlEscape(attributes[i][0]), xmlEscape(attributes[i][1]));
		}
		return String.format("<%s%s>", xmlEscape(nodeName), attributeString);

	}

	/**
	 * Write opening tag of XML node with a single specified attribute
	 * 
	 * @param nodeName       Name of the XML node
	 * @param attributeName  Attribute name
	 * @param attributeValue Attribute value
	 * @return opening tag of XML node
	 */
	public static String openNode(String nodeName, String attributeName, String attributeValue) {
		checkNullAndLength(nodeName, "Node name");
		String[][] attributes = { new String[] { attributeName, attributeValue } };
		return openNode(nodeName, attributes);

	}

	/**
	 * Write closing XML-tag for a node
	 */
	public static String closeNode(String nodeName) {
		checkNullAndLength(nodeName, "Node name");
		return "</" + xmlEscape(nodeName) + ">";
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
	public static String writeNode(String nodeName, String nodeContent, String attributeName, String attributeValue) {
		checkNullAndLength(nodeName, "Node name");
		nodeContent = nullToEmpty(nodeContent);
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
	public static String writeNode(String nodeName, String nodeContent, String[][] attributes) {
		checkNullAndLength(nodeName, "Node name");
		nodeContent = nullToEmpty(nodeContent);
		return String.format("%s%s%s", openNode(nodeName, attributes), nodeContent, closeNode(nodeName));
	}

	/**
	 * Write a full node with opening/closing tags and contents without attributes.
	 * 
	 * @param nodeName    Name of the XML node
	 * @param nodeContent Value of the node
	 * @return XML node with opening and closing tags and contents
	 */
	public static String writeNode(String nodeName, String nodeContent) {
		checkNullAndLength(nodeName, "Node name");
		nodeContent = nullToEmpty(nodeContent);
		return String.format("%s%s%s", openNode(nodeName), nodeContent, closeNode(nodeName));
	}

	private static String nullToEmpty(String input) {
		return input == null ? "" : input;
	}

	private static String checkNullAndLength(String input, String argumentName) {
		Objects.requireNonNull(input);
		if (input.equals(""))
			throw new IllegalArgumentException("'" + argumentName + "' must have length > 1");
		return input;
	}

}
