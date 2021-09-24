package com.amazonaws.mtxml;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amazonaws.mtxml.utils.MTUtils;
import com.amazonaws.mtxml.utils.XmlFactory;

/*
 * Class for modelling the basic headerblock in a MT message
 */
public class BasicHeaderBlock implements MTComponent {
	/**
	 * Regex pattern for identyfying the elements in the basic headerblock
	 */
	private final static String REGEX_PATTERN = "\\{(?<BlockIdentifier>1):(?<AppID>[A-Z])(?<ServiceID>0\\d|\\d{2})(?<LTAddress>[A-Z]{12})(?<SessionNumber>\\d{4})(?<SequenceNumber>\\d{6})\\}";

	/**
	 * Container for the tags
	 */
	private Map<String, String> data = new HashMap<String, String>();
	private Map<String, String> LTAddressData = new HashMap<String, String>();

	private void initMaps() {
		data.put("BlockIdentifier", null);
		data.put("AppID", null);
		data.put("ServiceID", null);
		data.put("LTAddress", null);
		data.put("SessionNumber", null);
		data.put("SequenceNumber", null);
		data.put("RawData", null);

		LTAddressData.put("BIC", null);
		LTAddressData.put("LogicalTerminal", null);
		LTAddressData.put("BIC8", null);
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
		putData(data, "BlockIdentifier", matcher.group("BlockIdentifier"));
		putData(data, "AppID", matcher.group("AppID"));
		putData(data, "ServiceID", matcher.group("ServiceID"));
		putData(data, "LTAddress", MTUtils.formatBIC(matcher.group("LTAddress")));
		putData(data, "SessionNumber", matcher.group("SessionNumber"));
		putData(data, "SequenceNumber", matcher.group("SequenceNumber"));
		putData(data, "RawData", content);

		// LT Address details
		String[] LTAddressSplit = MTUtils.splitLogicalTerminal(matcher.group("LTAddress"));
		putData(LTAddressData, "BIC", LTAddressSplit[0]);
		putData(LTAddressData, "LogicalTerminal", LTAddressSplit[1]);
		putData(LTAddressData, "BIC8", LTAddressSplit[2]);

	}

	/**
	 * Wrapper for adding found value pairs into container
	 * 
	 * @param container Container to put the data in
	 * @param key       Fieldname
	 * @param value     Value of the field
	 */
	private void putData(Map<String, String> container, String key, String value) {
		if (!container.containsKey(key)) {
			String msg = String.format("Container does not contain key '%s'.", key);
			throw new IllegalArgumentException(msg);
		}
		container.put(key, value == null ? "" : value);

	}

	/**
	 * Generalized getter for the different fields.
	 * 
	 * @param field Field to get
	 * @return Value of said field
	 */
	public String getData(String field) {

		Map<String, String> container;
		String fieldName;

		if (field.startsWith("LTAddress.")) {
			container = LTAddressData;
			fieldName = field.substring("LTAddress.".length());
		} else {
			container = data;
			fieldName = field;
		}

		if (!container.containsKey(fieldName)) {
			String msg = String.format("Invalid field '%s'.", field);
			throw new IllegalArgumentException(msg);
		}
		return container.get(fieldName);
	}

	/**
	 * Return a list of valid fields
	 */
	public String[] validFields() {
		return (String[]) data.keySet().toArray();
	}

	@Override
	public String toXml() {
		String xml = XmlFactory.openNode("BasicHeader");
		xml += XmlFactory.writeNode("ApplicationIdentifier", data.get("AppID"));
		xml += XmlFactory.writeNode("ServiceIdentifier", data.get("ServiceID"));
		xml += XmlFactory.writeNode("LTAddress", data.get("LTAddress"));

		xml += XmlFactory.openNode("LTAddress_Details");
		xml += XmlFactory.writeNode("BIC", LTAddressData.get("BIC"));
		xml += XmlFactory.writeNode("LogicalTerminal", LTAddressData.get("LogicalTerminal"));
		xml += XmlFactory.writeNode("BIC8", LTAddressData.get("BIC8"));
		xml += XmlFactory.closeNode("LTAddress_Details");

		xml += XmlFactory.writeNode("SessionNumber", data.get("SessionNumber"));
		xml += XmlFactory.writeNode("SequenceNumber", data.get("SequenceNumber"));
		xml += XmlFactory.closeNode("BasicHeader");
		return xml;
	}

	public static void main(String[] args) {
		new BasicHeaderBlock("{1:F01MYBABBICAXXX0878450607}").getData("LTAddress.BIC");
	}

}
