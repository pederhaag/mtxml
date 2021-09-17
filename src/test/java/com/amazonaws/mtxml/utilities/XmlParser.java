package com.amazonaws.mtxml.utilities;

import java.io.IOException;
import java.io.StringReader;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class XmlParser {
	public static void main(String[] args) throws JDOMException, IOException {
		String xml = "<message>HELLO!</message>";
		SAXBuilder saxBuilder = new SAXBuilder();
		Document doc = saxBuilder.build(new StringReader(xml));
		String message = doc.getRootElement().getText();
		System.out.println(message);
	}
}
