package com.amazonaws.mtxml;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BasicHeaderBlock implements MTComponent {
	private final static String regexPattern = "\\{(1):([A-Z])(0\\d|\\d{2})([A-Z]{12})(\\d{4})(\\d{6})\\}";

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
		if (content == null) {
			throw new NullPointerException("Argument cannot be null.");
		}
		
		initMaps();

		Pattern pattern = Pattern.compile(regexPattern);
		Matcher matcher = pattern.matcher(content);

		if (!matcher.find()) {
			throw new SyntaxException(regexPattern, content);
		}

		setData(data, "BlockIdentifier", matcher.group(1));
		setData(data, "AppID", matcher.group(2));
		setData(data, "ServiceID", matcher.group(3));
		setData(data, "LTAdress", matcher.group(4));
		setData(data, "SessionNumber", matcher.group(5));
		setData(data, "SequenceNumber", matcher.group(6));
		setData(data, "RawData", content);

	}

	private void setData(Map<String, String> container, String key, String value) {
		if (!data.containsKey(key)) {
			String msg = String.format("Container does not contain key '%s'.", key);
			throw new IllegalArgumentException(msg);
		}
		data.put(key, value);

	}

	public String getData(String field) {
		if (!data.containsKey(field)) {
			String msg = String.format("Invalid field '%s'.", field);
			throw new IllegalArgumentException(msg);
		}
		return data.get(field);
	}

//	public static void main(String[] args) {
//		BasicHeaderBlock block = new BasicHeaderBlock("{1:F01MYBABBICAXXX0878450607}");
//		System.out.println("ApplicationIdentifier: " + block.AppID);
//		System.out.println("ServiceID: " + block.ServiceID);
//		System.out.println("LTAdress: " + block.LTAdress);
//		System.out.println("SessionNumber: " + block.SessionNumber);
//		System.out.println("SequenceNumber: " + block.SequenceNumber);
//
//	}

	@Override
	public String toXml() {
		// TODO Auto-generated method stub
		return null;
	}

}
