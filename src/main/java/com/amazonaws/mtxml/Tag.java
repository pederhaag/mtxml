package com.amazonaws.mtxml;

import java.util.ArrayList;

class Tag implements MTComponent {
	private String qualifier;
	private ArrayList<String> fieldNames;
	private ArrayList<String> fieldValues;
	private String tagName;

	Tag(String tagName, ArrayList<String> fieldNames, ArrayList<String> fieldValues) {
		this.tagName = tagName;
		this.fieldNames = fieldNames;
		this.fieldValues = fieldValues;

	}

	Tag(String tagName, ArrayList<String> fieldNames, ArrayList<String> fieldValues, String qualifier) {
		this(tagName, fieldNames, fieldValues);
		this.qualifier = qualifier;
	}

	String getFieldValue(String field) {
		for (int i = 0; i < fieldNames.size(); i++) {
			if (fieldNames.get(i).equals(field))
				return fieldValues.get(i);
		}
		return null;
	}

	@Override
	public String toXml() {
		String opening = null;

		if (qualifier == null) {
			opening = XmlFactory.openNode("Tag" + tagName);
		} else {
			opening = XmlFactory.openNode("Tag" + tagName, "Qualifier", qualifier);
		}

		String content = "";
		for (int i = 0; i < fieldNames.size(); i++) {
			content += XmlFactory.writeNode(fieldNames.get(i), fieldValues.get(i));
		}

		String closing = XmlFactory.closeNode("Tag" + tagName);

		return opening + content + closing;
	}

	public String toString() {
		// TODO: Remove this..
		String s = "Tag: " + tagName;
		for (int i = 0; i < fieldNames.size(); i++) {
			s += "\n\t" + fieldNames.get(i) + ": " + fieldValues.get(i);
		}
		return s;
	}

}
