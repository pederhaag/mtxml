package com.amazonaws.mtxml;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amazonaws.mtxml.utils.MTUtils;

/**
 * Class for modelling the basic headerblock in a MT message
 */
public class BasicHeaderBlock extends MTTree {
	/**
	 * Regex pattern for identyfying the elements in the basic headerblock
	 */
	private final static String REGEX_PATTERN = "\\{(?<BlockIdentifier>1):(?<AppID>[A-Z])(?<ServiceID>0\\d|\\d{2})(?<LTAddress>[A-Z]{12})(?<SessionNumber>\\d{4})(?<SequenceNumber>\\d{6})\\}";

	BasicHeaderBlock(String content) {
		super("BasicHeader", null);

		Objects.requireNonNull(content, "Blockcontent cannot be null");

		// Match the block
		Pattern pattern = Pattern.compile(REGEX_PATTERN);
		Matcher matcher = pattern.matcher(content);

		if (!matcher.find()) {
			throw new MTSyntaxException(REGEX_PATTERN, content);
		}

		// Add found datafields
		addComponent(this, "ApplicationIdentifier", matcher.group("AppID"));
		addComponent(this, "ServiceIdentifier", matcher.group("ServiceID"));
		addComponent(this, "LTAddress", matcher.group("LTAddress"));

		// LT Address details
		String[] LTAddressSplit = MTUtils.splitLogicalTerminal(matcher.group("LTAddress"));
		MTTree LTAddressDetails = new MTTree("LTAddress_Details", null);
		addComponent(LTAddressDetails, "BIC", LTAddressSplit[0]);
		addComponent(LTAddressDetails, "LogicalTerminal", LTAddressSplit[1]);
		addComponent(LTAddressDetails, "BIC8", LTAddressSplit[2]);
		addComponent("LTAddress_Details", LTAddressDetails);

		addComponent(this, "SessionNumber", matcher.group("SessionNumber"));
		addComponent(this, "SequenceNumber", matcher.group("SequenceNumber"));

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
