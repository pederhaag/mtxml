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

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public String toXml() {
		// TODO Auto-generated method stub
		return null;
	}

	public String toString() {
		String s = "Tag: " + tagName;
		for (int i = 0; i < fieldNames.size(); i++) {
			s += "\n\t" + fieldNames.get(i) + ": " + fieldValues.get(i);
		}
		return s;
	}

}
