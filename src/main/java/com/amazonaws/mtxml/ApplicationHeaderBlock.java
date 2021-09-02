package com.amazonaws.mtxml;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * https://www.paiementor.com/swift-mt-message-block-2/
 */
public class ApplicationHeaderBlock implements MTComponent {
	private final static String regexPatternInput = "\\{(2):(I)(\\d{3})([A-Z0-9]{12})(S|U|N)?(1|2|3)?(\\d{3})?\\}";
	private final static String regexPatternOutput = "\\{(2):(O)(\\d{3})(\\d{4})(.{28})(\\d{6})(\\d{4})(S|U|N)\\}";
	private final static String regexPatternMIR = "^(\\d{6})([A-Z]{4}[A-Z]{2}[0-9A-Z]{2}[0-9A-Z][0-9A-Z]{3})(\\d{4})(\\d{6})";

	private Map<String, String> data = new HashMap<String, String>();
	private Map<String, String> mirData = new HashMap<String, String>();

	private void initMaps() {
		data.put("BlockIdentifier", null);
		data.put("InOutID", null);
		data.put("MT", null);

		data.put("DestAddress", null);
		data.put("Priority", null);
		data.put("DeliveryMonitoring", null);
		data.put("ObsolencePeriod", null);

		data.put("InputTime", null);
		data.put("OutputDate", null);
		data.put("OutputTime", null);
		data.put("RawData", null);
		data.put("MIR", null);

		mirData.put("SendersDate", null);
		mirData.put("LogicalTerminal", null);
		mirData.put("SessionNumber", null);
		mirData.put("SequenceNumber", null);
	}

	ApplicationHeaderBlock(String content) {
		if (content == null) {
			throw new NullPointerException("Argument cannot be null.");
		}

		initMaps();

		Pattern patternInput = Pattern.compile(regexPatternInput);
		Matcher matcherInput = patternInput.matcher(content);

		Pattern patternOutput = Pattern.compile(regexPatternOutput);
		Matcher matcherOutput = patternOutput.matcher(content);

		if (matcherInput.find()) {
			setData(data, "BlockIdentifier", matcherInput.group(1));
			setData(data, "InOutID", matcherInput.group(2));
			setData(data, "MT", matcherInput.group(3));
			setData(data, "DestAddress", matcherInput.group(4));
			setData(data, "Priority", matcherInput.group(5));
			setData(data, "DeliveryMonitoring", matcherInput.group(6));
			setData(data, "ObsolencePeriod", matcherInput.group(7));

		} else if (matcherOutput.find()) {
			setData(data, "BlockIdentifier", matcherOutput.group(1));
			setData(data, "InOutID", matcherOutput.group(2));
			setData(data, "MT", matcherOutput.group(3));
			setData(data, "InputTime", matcherOutput.group(4));
			setData(data, "MIR", matcherOutput.group(5));
			setData(data, "OutputDate", matcherOutput.group(6));
			setData(data, "OutputTime", matcherOutput.group(7));

			Matcher matcherMIR = Pattern.compile(regexPatternMIR).matcher(getData("MIR"));
			if (!matcherMIR.find()) {
				throw new SyntaxException(regexPatternMIR, content);
			}
			setData(mirData, "SendersDate", matcherMIR.group(1));
			setData(mirData, "LogicalTerminal", matcherMIR.group(2));
			setData(mirData, "SessionNumber", matcherMIR.group(3));
			setData(mirData, "SequenceNumber", matcherMIR.group(4));
		} else {
			throw new SyntaxException(regexPatternInput, content);
		}

	}

	private void setData(Map<String, String> container, String key, String value) {
		if (!container.containsKey(key)) {
			String msg = String.format("Container does not contain key '%s'.", key);
			throw new IllegalArgumentException(msg);
		}
		container.put(key, value);
	}

	public String getData(String field) {
		Map<String, String> container;
		String fieldContainer;

		if (field.startsWith("MIR.")) {
			container = mirData;
			fieldContainer = field.substring("MIR.".length());
		} else {
			container = data;
			fieldContainer = field;
		}
		if (!container.containsKey(fieldContainer)) {
			String msg = String.format("Invalid field '%s'.", fieldContainer);
			throw new IllegalArgumentException(msg);
		}
		return container.get(fieldContainer);
	}

	public static void main(String[] args) {
		ApplicationHeaderBlock block = new ApplicationHeaderBlock(
				"{2:O9400144210831BANKBICSAXXX61563916672108310144N}");

	}

	@Override
	public String toXml() {
		// TODO Auto-generated method stub
		return null;
	}

}
