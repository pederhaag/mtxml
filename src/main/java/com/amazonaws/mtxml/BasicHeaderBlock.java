package com.amazonaws.mtxml;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Class for modelling the basic headerblock in a MT message
 */
public class BasicHeaderBlock implements MTComponent {
	/**
	 * Regex pattern for identyfying the elements in the basic headerblock
	 */
	private final static String REGEX_PATTERN = "\\{(?<BlockIdentifier>1):(?<AppID>[A-Z])(?<ServiceID>0\\d|\\d{2})(?<LTAdress>[A-Z]{12})(?<SessionNumber>\\d{4})(?<SequenceNumber>\\d{6})\\}";

	/**
	 * Container for the tags
	 */
	private Map<String, String> data = new HashMap<String, String>();

	private void initMaps() {
		data.put("BlockIdentifier", null);
		data.put("AppID", null);
		data.put("ServiceID", null);
		data.put("LTAdress", null);
		data.put("SessionNumber", null);
		data.put("SequenceNumber", null);
		data.put("RawData", null);
	}

	BasicHeaderBlock(String content) {
		Objects.requireNonNull(content, "Blockcontent cannot be null");

		initMaps();

		// Match the block
		Pattern pattern = Pattern.compile(REGEX_PATTERN);
		Matcher matcher = pattern.matcher(content);

		if (!matcher.find()) {
			throw new MTSyntaxException(REGEX_PATTERN, content);
		}

		// Add found datafields
		setData(data, "BlockIdentifier", matcher.group("BlockIdentifier"));
		setData(data, "AppID", matcher.group("AppID"));
		setData(data, "ServiceID", matcher.group("ServiceID"));
		setData(data, "LTAdress", matcher.group("LTAdress"));
		setData(data, "SessionNumber", matcher.group("SessionNumber"));
		setData(data, "SequenceNumber", matcher.group("SequenceNumber"));
		setData(data, "RawData", content);

	}

	/**
	 * Wrapper for adding found value pairs into container
	 * 
	 * @param container Container to put the data in
	 * @param key       Fieldname
	 * @param value     Value of the field
	 */
	private void setData(Map<String, String> container, String key, String value) {
		if (!data.containsKey(key)) {
			String msg = String.format("Container does not contain key '%s'.", key);
			throw new IllegalArgumentException(msg);
		}
		data.put(key, value);

	}

	/**
	 * Generalized getter for the different fields.
	 * 
	 * @param field Field to get
	 * @return Value of said field
	 */
	public String getData(String field) {
		if (!data.containsKey(field)) {
			String msg = String.format("Invalid field '%s'.", field);
			throw new IllegalArgumentException(msg);
		}
		return data.get(field);
	}

	/**
	 * Return a list of valid fields
	 */
	public String[] validFields() {
		return (String[]) data.keySet().toArray();
	}

	@Override
	public String toXml() {
		String xml = XmlFactory.openNode("BasicHeaderBlock");
		xml += XmlFactory.writeNode("AppID", data.get("AppID"));
		xml += XmlFactory.writeNode("ServiceID", data.get("ServiceID"));
		xml += XmlFactory.writeNode("LTAdress", data.get("LTAdress"));
		xml += XmlFactory.writeNode("SessionNumber", data.get("SessionNumber"));
		xml += XmlFactory.writeNode("SequenceNumber", data.get("SequenceNumber"));
		xml += XmlFactory.closeNode("BasicHeaderBlock");
		return xml;
	}

}
