package com.amazonaws.mtxml;

import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * {@code Tag} models a individual tag in a SWIFT MT message. It often contains different subfields.
 * 
 */
class Tag implements MTComponent {
	/**
	 * Container for fieldnames
	 */
	private ArrayList<String> fieldNames;

	/**
	 * Container for fieldvalues
	 */
	private ArrayList<String> fieldValues;

	/**
	 * The name of the tag, i.e. 19A, 61 etc.
	 */
	private String tagName;

	/**
	 * Defines a set of fieldnames which are to be considered numeric and will
	 * therefore be subject to additional validation in the {@code TagFactory}
	 * class. {@see TagFactory#validateNumericField} {@see isNumericField}
	 */
	private final static String[] NUMERIC_FIELDS = new String[] { "Amount", "Quantity", "Rate", "Price", "Balance" };

	Tag(String tagName, ArrayList<String> fieldNames, ArrayList<String> fieldValues) {
		Objects.requireNonNull(fieldNames);
		Objects.requireNonNull(fieldValues);

		this.tagName = tagName;
		this.fieldNames = fieldNames;
		this.fieldValues = fieldValues;

		for (int i = 0; i < fieldNames.size(); i++) {
			if (isNumericField(fieldNames.get(i))) {
				fieldValues.set(i, fieldValues.get(i).replace(',', '.'));
			}
		}

	}

	/**
	 * Check if fieldname corresponds to a numeric-type field.
	 */
	public static boolean isNumericField(String fieldName) {
		for (String numberField : Tag.NUMERIC_FIELDS) {
			if (fieldName.equals(numberField))
				return true;
		}
		return false;
	}

	/**
	 * Getter for subfields.
	 * 
	 * @param field Field to Get
	 * @return Field value. Returns {@code null} if field is not part of
	 *         tag-definition. Returns an empty string if field is part of
	 *         definition and optional but is empty.
	 */
	public String getFieldValue(String field) {
		if (field.equals("Tag"))
			return tagName;
		for (int i = 0; i < fieldNames.size(); i++) {
			if (fieldNames.get(i).equals(field))
				return fieldValues.get(i);
		}
		return null;
	}

	@Override
	public String toXml() {
		String xmlOpening = null;
		String qualifier = null;

		ArrayList<String> fieldValuesToXml = new ArrayList<String>(fieldValues);
		ArrayList<String> fieldNamesToXml = new ArrayList<String>(fieldNames);

		// If the qualifier is found, remove it from the children-list
		if (fieldNamesToXml.size() > 0) {
			if (fieldNamesToXml.get(0).equals("Qualifier")) {
				qualifier = fieldValuesToXml.remove(0);
				fieldNamesToXml.remove(0);
			}
		}

		// Write the opening xml-tag
		if (qualifier == null) {
			xmlOpening = XmlFactory.openNode("Tag" + tagName);
		} else {
			xmlOpening = XmlFactory.openNode("Tag" + tagName, "Qualifier", qualifier);
		}

		// Loop through the fields and create children-nodes
		String xmlChildren = "";
		for (int i = 0; i < fieldNamesToXml.size(); i++) {

			String fieldValue = fieldValuesToXml.get(i);
			String fieldName = fieldNamesToXml.get(i);

			if (fieldValue.contains("\n")) {
				xmlChildren += XmlFactory.openNode(fieldName);
				// Multiline fields are split into children "Line" nodes
				for (String line : fieldValue.split("\n")) {
					xmlChildren += XmlFactory.writeNode("Line", line);
				}
				xmlChildren += XmlFactory.closeNode(fieldName);
			} else {
				xmlChildren += XmlFactory.writeNode(fieldName, fieldValue);
			}
		}

		// Close the root node of the tag
		String xmlClosing = XmlFactory.closeNode("Tag" + tagName);

		return xmlOpening + xmlChildren + xmlClosing;
	}

	public String toString() {
		String s = "Tag: " + tagName;
		for (int i = 0; i < fieldNames.size(); i++) {
			s += "\n\t" + fieldNames.get(i) + ": " + fieldValues.get(i);
		}
		return s;
	}

}
