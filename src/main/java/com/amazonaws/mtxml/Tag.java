package com.amazonaws.mtxml;

import java.util.LinkedHashMap;
import java.util.Objects;

/**
 *
 * {@code Tag} models a individual tag in a SWIFT MT message. It often contains
 * different subfields.
 * 
 */
class Tag extends MTTree {

	/**
	 * The name of the tag, i.e. 19A, 61 etc.
	 */
	private String tagName;

	/**
	 * The content of the tag in ordinary MT-format
	 */
	private String rawContent;

	/**
	 * Defines a set of fieldnames which are to be considered numeric and will
	 * therefore be subject to additional validation in the {@code TagFactory}
	 * class. {@see TagFactory#validateNumericField} {@see isNumericField}
	 */
	private final static String[] NUMERIC_FIELDS = new String[] { "Amount", "Quantity", "Rate", "Price", "Balance" };

	Tag(String tagName, String rawContent, String qualifier, LinkedHashMap<String, MTComponent> components) {
		super("Tag" + tagName, qualifier, components);
		Objects.requireNonNull(tagName);
		Objects.requireNonNull(components);

		this.tagName = tagName;
		this.rawContent = rawContent;

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
	 * Return the format of the tag as it would be presented in an MT message
	 */
	@Override
	public String toString() {
		return String.format(":%s:%s", tagName, rawContent);
	}

	@Override
	public String getData(String fieldName) {
		return fieldName.equals("Tag") ? tagName : super.getData(fieldName);
	}

}
