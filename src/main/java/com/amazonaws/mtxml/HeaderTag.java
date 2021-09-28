package com.amazonaws.mtxml;

import java.util.Objects;

import com.amazonaws.mtxml.utils.XmlFactory;

/**
 * Class for modelling the individual tags in headers of the form
 * {@code {t_i:v_i}} occurring in {@code TrailerHeaderBlock} and
 * {@code UserHeaderBlock} classes
 */
class HeaderTag implements MTComponent {
	/**
	 * {@code String} identifying the tag
	 */
	private final String tag;

	/**
	 * Valeu of the tag
	 */
	private final String value;

	/**
	 * Name of the resulting root node of the XML-representation
	 */
	private final String nodeName;

	/**
	 * Name of the XML node containing the tag description
	 */
	private final String tagNodeName;

	/**
	 * Name of the XML node containing the tag value
	 */
	private final String valueNodeName;

	HeaderTag(String tag, String value, String nodeName, String tagNodeName, String valueNodeName) {
		Objects.requireNonNull(tag, "Tag cannot be null");
		Objects.requireNonNull(value, "Tagvalue cannot be null");
		Objects.requireNonNull(nodeName, "Node name cannot be null");
		Objects.requireNonNull(tagNodeName, "Tag node name cannot be null");

		requireNonEmpty(tag, "tag");
		requireNonEmpty(nodeName, "nodeName");
		requireNonEmpty(tagNodeName, "tagNodeName");

		this.tag = tag;
		this.value = value;
		this.nodeName = nodeName;
		this.tagNodeName = tagNodeName;
		this.valueNodeName = valueNodeName;
	}

	@Override
	public String toXml() {
		return XmlFactory.openNode(nodeName) + XmlFactory.writeNode(tagNodeName, getTag())
				+ XmlFactory.writeNode(valueNodeName, getValue()) + XmlFactory.closeNode(nodeName);
	}

	@Override
	public String getValue() {
		return value;
	}

	public String getTag() {
		return tag;
	}

	/**
	 * Helper method for validating parameter inputs in
	 * {@link #HeaderTag(String, String, String, String, String)}
	 * 
	 * @param input
	 * @param fieldName Name of field. Needed for creating a useful exception
	 *                  description
	 */
	private static void requireNonEmpty(String input, String fieldName) {
		if (input.equals("")) {
			String msg = String.format("Field '%s' must be non-empty", fieldName);
			throw new IllegalArgumentException(msg);
		}
	}

}
