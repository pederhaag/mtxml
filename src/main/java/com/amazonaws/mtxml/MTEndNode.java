package com.amazonaws.mtxml;

import com.amazonaws.mtxml.utils.XmlFactory;

/**
 * The {@code MTEndNode} class is used to model a node in a MT message which
 * does not have any underlying subcomponents. It may for example be a subfield
 * in a {@code Tag} class.
 *
 */
public class MTEndNode implements MTComponent {
	/**
	 * Name of the resulting root node of the XML-representation
	 */
	private String nodeName;

	/**
	 * The value of the node
	 */
	private String value;

	/**
	 * Qualifier of the node
	 */
	private String qualifier;

	MTEndNode(String nodeName, String qualifier, String value) {
		this.nodeName = nodeName;
		this.qualifier = qualifier;
		this.value = value;
	}

	@Override
	public String toXml() {
		// null values are outputed as empty strings
		if (value == null)
			return "";

		String xmlContent = value;
		// Multi-line values are split into 'Line' children-nodes
		if (value.contains("\n")) {
			StringBuilder sb = new StringBuilder();
			for (String line : value.split("\n")) {
				sb.append(XmlFactory.writeNode("Line", line));
			}
			xmlContent = sb.toString();
		}

		if (qualifier == null) {
			return XmlFactory.writeNode(nodeName, xmlContent);
		} else {
			return XmlFactory.writeNode(nodeName, xmlContent, "Qualifier", qualifier);
		}
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return getValue();
	}

}
