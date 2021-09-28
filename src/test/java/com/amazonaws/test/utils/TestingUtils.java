package com.amazonaws.test.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.xmlunit.assertj3.XmlAssert;

public class TestingUtils {

	public static final String testFolderPath = new File("src/test").getAbsolutePath() + "/";
	public static final String testResourcesPath = new File(testFolderPath + "resources").getAbsolutePath() + "/";

	public static void assertXMLEqualIgnoreOrder(String testXml, String controlXml) {
		XmlAssert.assertThat(testXml).and(controlXml).ignoreChildNodesOrder().areIdentical();
	}

	public static void assertXMLEqual(String testXml, String controlXml) {
		XmlAssert.assertThat(testXml).and(controlXml).ignoreWhitespace().areIdentical();
	}

	public static void assertXMLEqualIgnoreFormat(String testXml, String controlXml) {
		XmlAssert.assertThat(testXml).and(controlXml).ignoreWhitespace();
	}

	public static void assertXPathEqual(String testxml, String xPath, String expectedValue) {
		XmlAssert.assertThat(testxml).valueByXPath(xPath).isEqualTo(expectedValue);
	}

	public static String readFileContent(String filepath) throws FileNotFoundException {
		String output = "";
		File file = new File(filepath);

		try (Scanner sc = new Scanner(file)) {
			while (sc.hasNextLine()) {
				output += sc.nextLine();
				if (sc.hasNextLine())
					output += "\n";
			}
		}

		return output;
	}

}
