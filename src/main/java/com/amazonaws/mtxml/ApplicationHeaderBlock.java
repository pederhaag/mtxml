package com.amazonaws.mtxml;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Class for modelling the application headerblock in a MT message
 */
public class ApplicationHeaderBlock implements MTComponent {
	/**
	 * Regex pattern for identyfying the components of a app. headerblock for
	 * input-messages
	 */
	private final static String REGEX_INPUT = "\\{(?<BlockIdentifier>2):(?<InOutID>I)(?<MT>\\d{3})(?<DestAddress>[A-Z0-9]{12})(?<Priority>S|U|N)?(?<DeliveryMonitoring>1|2|3)?(?<ObsolencePeriod>\\d{3})?\\}";

	/**
	 * Regex pattern for identyfying the components of a app. headerblock for
	 * output-messages
	 */
	private final static String REGEX_OUTPUT = "\\{(?<BlockIdentifier>2):(?<InOutID>O)(?<MT>\\d{3})(?<InputTime>\\d{4})(?<MIR>.{28})(?<OutputDate>\\d{6})(?<OutputTime>\\d{4})(?<Priority>S|U|N)\\}";

	/**
	 * Regex pattern for identyfying the components of the MIR-section
	 */
	private final static String REGEX_MIR = "^(?<SendersDate>\\d{6})(?<LogicalTerminal>[A-Z]{4}[A-Z]{2}[0-9A-Z]{2}[0-9A-Z][0-9A-Z]{3})(?<SessionNumber>\\d{4})(?<SequenceNumber>\\d{6})";

	/**
	 * Containers with the data for the different components
	 */
	private Map<String, String> data = new HashMap<String, String>();
	private Map<String, String> mirData = new HashMap<String, String>();

	/*
	 * Method for defining which subfields to expect
	 */
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
		Objects.requireNonNull(content, "Blockcontent cannot be null");

		initMaps();

		Pattern patternInput = Pattern.compile(REGEX_INPUT);
		Matcher matcherInput = patternInput.matcher(content);

		Pattern patternOutput = Pattern.compile(REGEX_OUTPUT);
		Matcher matcherOutput = patternOutput.matcher(content);

		// Determine if it is an input or output message
		if (matcherInput.find()) {
			putData(data, "BlockIdentifier", matcherInput.group("BlockIdentifier"));
			putData(data, "InOutID", matcherInput.group("BlockIdentifier"));
			putData(data, "MT", matcherInput.group("MT"));
			putData(data, "DestAddress", matcherInput.group("DestAddress"));
			putData(data, "Priority", matcherInput.group("Priority"));
			putData(data, "DeliveryMonitoring", matcherInput.group("DeliveryMonitoring"));
			putData(data, "ObsolencePeriod", matcherInput.group("ObsolencePeriod"));

		} else if (matcherOutput.find()) {

			putData(data, "BlockIdentifier", matcherOutput.group("BlockIdentifier"));
			putData(data, "InOutID", matcherOutput.group("InOutID"));
			putData(data, "MT", matcherOutput.group("MT"));
			putData(data, "InputTime", matcherOutput.group("InputTime"));
			putData(data, "MIR", matcherOutput.group("MIR"));
			putData(data, "OutputDate", matcherOutput.group("OutputDate"));
			putData(data, "OutputTime", matcherOutput.group("OutputTime"));
			putData(data, "Priority", matcherOutput.group("Priority"));

			Matcher matcherMIR = Pattern.compile(REGEX_MIR).matcher(getData("MIR"));
			if (!matcherMIR.find()) {
				throw new MTSyntaxException(REGEX_MIR, content);
			}
			putData(mirData, "SendersDate", matcherMIR.group("SendersDate"));
			putData(mirData, "LogicalTerminal", matcherMIR.group("LogicalTerminal"));
			putData(mirData, "SessionNumber", matcherMIR.group("SessionNumber"));
			putData(mirData, "SequenceNumber", matcherMIR.group("SequenceNumber"));
		} else {
			throw new MTSyntaxException(REGEX_INPUT, content);
		}

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
		container.put(key, value);
	}

	/**
	 * Generalized getter for the different fields. Note that MIR-subfields are
	 * prefixed with "MIR." - i.e. getData("MIR.SendersDate");
	 * 
	 * @param field Field to get
	 * @return Value of said field
	 */
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

	/**
	 * Return a list of valid fields
	 */
	public String[] validFields() {
		String[] fields = new String[data.keySet().size() + mirData.keySet().size()];
		int i = 0;
		for (String field : data.keySet()) {
			fields[i] = field;
			i++;
		}
		for (String mirField : mirData.keySet()) {
			fields[i] = "MIR." + mirField;
			i++;
		}
		return fields;
	}


	@Override
	public String toXml() {
		String xml = XmlFactory.openNode("ApplicationHeaderBlock");
		
		if (data.get("InOutID").equals("I")) {
			xml += XmlFactory.writeNode("InOutID", data.get("InOutID"));
			xml += XmlFactory.writeNode("MT", data.get("MT"));
			xml += XmlFactory.writeNode("DestAddress", data.get("DestAddress"));
			xml += XmlFactory.writeNode("Priority", data.get("Priority"));
			xml += XmlFactory.writeNode("DeliveryMonitoring", data.get("DeliveryMonitoring"));
			xml += XmlFactory.writeNode("ObsolencePeriod", data.get("ObsolencePeriod"));
			
		} else {
			xml += XmlFactory.writeNode("InOutID", data.get("InOutID"));
			xml += XmlFactory.writeNode("MT", data.get("MT"));
			xml += XmlFactory.writeNode("InputTime", data.get("InputTime"));
			xml += XmlFactory.writeNode("MIR", data.get("MIR"));
			xml += mirToXml();
			xml += XmlFactory.writeNode("OutputDate", data.get("OutputDate"));
			xml += XmlFactory.writeNode("OutputTime", data.get("OutputTime"));
			xml += XmlFactory.writeNode("Priority", data.get("Priority"));

		}

		xml += XmlFactory.closeNode("ApplicationHeaderBlock");
		return xml;
	}

	private String mirToXml() {
		String xml = XmlFactory.openNode("MIR_Details");
		xml += XmlFactory.writeNode("SendersDate", mirData.get("SendersDate"));
		xml += XmlFactory.writeNode("LogicalTerminal", mirData.get("LogicalTerminal"));
		xml += XmlFactory.writeNode("SessionNumber", mirData.get("SessionNumber"));
		xml += XmlFactory.writeNode("SequenceNumber", mirData.get("SequenceNumber"));
		xml += XmlFactory.closeNode("MIR_Details");
		return xml;
	}

	public static void main(String[] args) {
		ApplicationHeaderBlock block = new ApplicationHeaderBlock(
				"{2:O9400144210831BANKBICSAXXX61563916672108310144N}");
		System.out.println(block.toXml());

	}

}
