package com.amazonaws.mtxml;

import java.util.ArrayList;
import java.util.Objects;

class Tag implements MTComponent {
//	private String qualifier;
	private ArrayList<String> fieldNames;
	private ArrayList<String> fieldValues;
	private String tag;

	Tag(String tagName, ArrayList<String> fieldNames, ArrayList<String> fieldValues) {
		Objects.requireNonNull(fieldNames);
		Objects.requireNonNull(fieldValues);

		this.tag = tagName;
		this.fieldNames = fieldNames;
		this.fieldValues = fieldValues;

	}

	String getFieldValue(String field) {
		if (field.equals("Tag"))
			return tag;
		for (int i = 0; i < fieldNames.size(); i++) {
			if (fieldNames.get(i).equals(field))
				return fieldValues.get(i);
		}
		return null;
	}

	@Override
	public String toXml() {
		String opening = null;
		String qualifier = null;

		ArrayList<String> fieldValuesToXml = fieldValues;
		ArrayList<String> fieldNamesToXml = fieldNames;

		if (fieldNamesToXml.size() > 0) {
			if (fieldNamesToXml.get(0).equals("Qualifier")) {
				qualifier = fieldValuesToXml.remove(0);
				fieldNamesToXml.remove(0);

			}

		}

		if (qualifier == null) {
			opening = XmlFactory.openNode("Tag" + tag);
		} else {
			opening = XmlFactory.openNode("Tag" + tag, "Qualifier", qualifier);
		}

		String content = "";

		for (int i = 0; i < fieldNamesToXml.size(); i++) {

			String fieldValue = fieldValuesToXml.get(i);
			String fieldName = fieldNamesToXml.get(i);
			
			if (fieldValue.contains("\n")) {
				content += XmlFactory.openNode(fieldName);
				for (String line : fieldValue.split("\n")) {
					content += XmlFactory.writeNode("Line", line);
				}
				content += XmlFactory.closeNode(fieldName);
			} else {
				content += XmlFactory.writeNode(fieldName, fieldValue);
			}
		}

		String closing = XmlFactory.closeNode("Tag" + tag);

		return opening + content + closing;
	}

	public String toString() {
		String s = "Tag: " + tag;
		for (int i = 0; i < fieldNames.size(); i++) {
			s += "\n\t" + fieldNames.get(i) + ": " + fieldValues.get(i);
		}
		return s;
	}

}
