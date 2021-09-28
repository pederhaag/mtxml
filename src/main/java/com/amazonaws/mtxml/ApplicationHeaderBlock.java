package com.amazonaws.mtxml;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amazonaws.mtxml.utils.MTUtils;

/**
 * Class for modelling the application headerblock in a MT message
 */
public class ApplicationHeaderBlock extends MTTree {
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

	ApplicationHeaderBlock(String content) {
		super("ApplicationHeader", null);

		Objects.requireNonNull(content, "Blockcontent cannot be null");

		Pattern patternInput = Pattern.compile(REGEX_INPUT);
		Matcher matcherInput = patternInput.matcher(content);

		Pattern patternOutput = Pattern.compile(REGEX_OUTPUT);
		Matcher matcherOutput = patternOutput.matcher(content);

		// Determine if it is an input or output message
		if (matcherInput.find()) {
			// Populate input-data
			addComponent(this, "InputOutputIdentifier", matcherInput.group("InOutID"));
			addComponent(this, "MessageType", matcherInput.group("MT"));
			addComponent(this, "DestAddress", matcherInput.group("DestAddress"));

			// Additional details on the Destination address
			String[] destAddressSplit = MTUtils.splitLogicalTerminal(matcherInput.group("DestAddress"));
			MTTree destAddressDetails = new MTTree("DestAddress_Details", null);
			addComponent(destAddressDetails, "BIC", destAddressSplit[0]);
			addComponent(destAddressDetails, "LogicalTerminal", destAddressSplit[1]);
			addComponent(destAddressDetails, "BIC8", destAddressSplit[2]);
			addComponent("DestAddress_Details", destAddressDetails);

			// Populate input-data continued
			addComponent(this, "Priority", matcherInput.group("Priority"));
			addComponent(this, "DeliveryMonitoring", matcherInput.group("DeliveryMonitoring"));
			addComponent(this, "ObsolencePeriod", matcherInput.group("ObsolencePeriod"));
		} else if (matcherOutput.find()) {
			// Populate output-data
			addComponent(this, "InputOutputIdentifier", matcherOutput.group("InOutID"));
			addComponent(this, "MessageType", matcherOutput.group("MT"));
			addComponent(this, "InputTime", matcherOutput.group("InputTime"));
			addComponent(this, "MIR", matcherOutput.group("MIR"));

			// Additional details on the MIR
			Matcher matcherMIR = Pattern.compile(REGEX_MIR).matcher(matcherOutput.group("MIR"));
			if (!matcherMIR.find()) {
				throw new MTSyntaxException(REGEX_MIR, content);
			}
			MTTree MIRDetails = new MTTree("MIR_Details", null);
			addComponent(MIRDetails, "SendersDate", matcherMIR.group("SendersDate"));
			addComponent(MIRDetails, "LogicalTerminal", matcherMIR.group("LogicalTerminal"));
			addComponent(MIRDetails, "SessionNumber", matcherMIR.group("SessionNumber"));
			addComponent(MIRDetails, "SequenceNumber", matcherMIR.group("SequenceNumber"));
			addComponent("MIR_Details", MIRDetails);

			addComponent(this, "OutputDate", matcherOutput.group("OutputDate"));
			addComponent(this, "OutputTime", matcherOutput.group("OutputTime"));
			addComponent(this, "Priority", matcherOutput.group("Priority"));

		} else {
			throw new MTSyntaxException(content + " does not match the applicationheader format");
		}

	}

	/**
	 * Helper method for reducing spaghetti in constructor. See
	 * {@link MTTree#addComponent(String, MTComponent)}
	 * 
	 * @param comp     Component to add subcomponent to
	 * @param nodeName Name of the node of the subcomponent to add
	 * @param value    Value of the subcomponent to add
	 */
	private void addComponent(MTTree comp, String nodeName, String value) {
		String NodeValue = value == null ? "" : value;
		comp.addComponent(nodeName, new MTEndNode(nodeName, null, NodeValue));

	}

}
