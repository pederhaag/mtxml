package com.amazonaws.test.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.mtxml.utils.XmlFactory;

import de.siegmar.fastcsv.reader.CommentStrategy;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvReader.CsvReaderBuilder;
import de.siegmar.fastcsv.reader.CsvRow;

public class TestCases {
	private static String[] validTrailerBlockFiles = { "trailerblockinput1.csv", "trailerblockinput2.csv" };
	private static String[] validUserHeaderBlockFiles = { "userheaderblockinput1.csv", "userheaderblockinput2.csv" };

	private static final String validTextBlockFolder = TestingUtils.testResourcesPath + "validtextblocks/";

	private final static String validTagFile = new File(TestingUtils.testResourcesPath + "tags/validTags.txt")
			.getAbsolutePath();

	public static List<Map<String, String>> getValidMtTestCases() throws Exception {
		ArrayList<String> basicHeaderBlocks = new ArrayList<String>();
		getValidBasicHeaderBlockTestCases().forEach((m) -> basicHeaderBlocks.add(m.get("rawContent")));

		ArrayList<String> appHeaderBlocks = new ArrayList<String>();
		getValidInputApplicationHeaderBlockTestCases().forEach((m) -> appHeaderBlocks.add(m.get("rawContent")));
		getValidOutputApplicationHeaderBlockTestCases().forEach((m) -> appHeaderBlocks.add(m.get("rawContent")));

		ArrayList<String> userHeaderBlocks = new ArrayList<String>();
		getValidUserHeaderBlockTestCases().forEach((k, v) -> userHeaderBlocks.add(k));

		ArrayList<String> textBlocks = new ArrayList<String>();
		getValidTextBlockTestCases().forEach((s) -> textBlocks.add(s[0]));

		ArrayList<String> trailerBlocks = new ArrayList<String>();
		getValidTrailerBlockTestCases().forEach((k, v) -> trailerBlocks.add(k));

		List<Map<String, String>> mtTestCases = new ArrayList<Map<String, String>>();

		for (String basicHeaderBlock : basicHeaderBlocks)
			for (String appHeaderBlock : appHeaderBlocks)
				for (String userHeaderBlock : userHeaderBlocks)
					for (String textBlock : textBlocks)
						for (String trailerBlock : trailerBlocks) {
							Map<String, String> mtTestCase = new HashMap<String, String>();
							mtTestCase.put("BasicHeaderBlock", basicHeaderBlock);
							mtTestCase.put("ApplicationHeaderBlock", appHeaderBlock);
							mtTestCase.put("UserHeaderBlock", userHeaderBlock);
							mtTestCase.put("TextBlock", textBlock);
							mtTestCase.put("TrailerBlock", trailerBlock);
							mtTestCase.put("MT",
									basicHeaderBlock + appHeaderBlock + userHeaderBlock + textBlock + trailerBlock);
							mtTestCases.add(mtTestCase);
						}

		return mtTestCases;

	}

	public static Map<String, ArrayList<String[]>> getValidUserHeaderBlockTestCases() throws IOException {
		String testcaseFolderPath = TestingUtils.testResourcesPath + "validuserheaderblocks/";
		return buildTestcasesFromCSV(testcaseFolderPath, validUserHeaderBlockFiles, "3");
	}

	public static Map<String, ArrayList<String[]>> getValidTrailerBlockTestCases() throws IOException {
		String testcaseFolderPath = TestingUtils.testResourcesPath + "validtrailerblocks/";
		return buildTestcasesFromCSV(testcaseFolderPath, validTrailerBlockFiles, "5");
	}

	public static String[] getInvalidApplicationHeaderBlockTestCases() {
		String invalidOutputBlock1 = "{3:O9400144210831BANKBICSAXXX61563916672108310144N}";
		String invalidOutputBlock2 = "{2:O5640004210831MYBANKTUBBRA99001234560101012000}";
		String invalidInputBlock1 = "{2:I527ABCDEFGHXBR?N}";
		String invalidInputBlock2 = "{2:I99DDDDEFGHXXXXN}";

		return new String[] { invalidOutputBlock1, invalidOutputBlock2, invalidInputBlock1, invalidInputBlock2 };
	}

	public static String[] getInvalidBasicHeaderBlockTestCases() {
		return new String[] { "{2:F01MYBABBICAXXX0878450607}", "{1:F01MYBABBICAXXX08784506078}",
				"{1:F01MYBABBICAXXX08S8450607}" };
	}

	public static String[] getInvalidTrailerBlockTestCases() {
		return new String[] { "{?:{123:321}{999:000}}", "{3:}", "" };
	}

	public static String[] getInvalidUserHeaderBlockTestCases() {
		return new String[] { "{1:{123:321}{999:000}}", "{3:}", "" };
	}

	public static ArrayList<String[]> getValidHeaderTags() {
		ArrayList<String[]> tags = new ArrayList<String[]>();

		tags.add(new String[] { "123", "SOMETHING" });
		tags.add(new String[] { "111", "BLABLA" });
		tags.add(new String[] { "222", "noreg" });
		tags.add(new String[] { "ok", "num321" });
		tags.add(new String[] { "001", "1n2n3" });
		tags.add(new String[] { "002", "12312542365-64843524" });
		tags.add(new String[] { "003", "XXX-XXX-XXX?" });

		return tags;
	}

	public static ArrayList<Map<String, String>> getValidInputApplicationHeaderBlockTestCases() throws Exception {
		ArrayList<Map<String, String>> validBlocks = new ArrayList<Map<String, String>>();
		Map<String, String> blockData;

		// Input Block 1
		blockData = new HashMap<String, String>();
		blockData.put("rawContent", "{2:I527ABCDEFGHXBRAN3}");
		blockData.put("BlockIdentifier", "2");
		blockData.put("InOutID", "I");
		blockData.put("MT", "527");
		blockData.put("DestAddress", "ABCDEFGHXBRA");
		blockData.put("Priority", "N");
		blockData.put("DeliveryMonitoring", "3");
		blockData.put("ObsolencePeriod", "");
		blockData.put("xml", expectedXmlApplicationHeader(blockData));
		validBlocks.add(blockData);

		// Input Block 2
		blockData = new HashMap<String, String>();
		blockData.put("rawContent", "{2:I299DDDDEFGHXXXXU007}");
		blockData.put("BlockIdentifier", "2");
		blockData.put("InOutID", "I");
		blockData.put("MT", "299");
		blockData.put("DestAddress", "DDDDEFGHXXXX");
		blockData.put("Priority", "U");
		blockData.put("DeliveryMonitoring", "");
		blockData.put("ObsolencePeriod", "007");
		blockData.put("xml", expectedXmlApplicationHeader(blockData));
		validBlocks.add(blockData);

		return validBlocks;
	}

	public static ArrayList<Map<String, String>> getValidOutputApplicationHeaderBlockTestCases() throws Exception {
		ArrayList<Map<String, String>> validBlocks = new ArrayList<Map<String, String>>();
		Map<String, String> blockData;

		// Output Block 1
		blockData = new HashMap<String, String>();
		blockData.put("rawContent", "{2:O9400144210831BANKBICSAXXX61563916672108310144N}");
		blockData.put("BlockIdentifier", "2");
		blockData.put("InOutID", "O");
		blockData.put("MT", "940");
		blockData.put("InputTime", "0144");
		blockData.put("MIR", "210831BANKBICSAXXX6156391667");
		blockData.put("OutputDate", "210831");
		blockData.put("OutputTime", "0144");
		blockData.put("Priority", "N");
		blockData.put("MIR.SendersDate", "210831");
		blockData.put("MIR.LogicalTerminal", "BANKBICSAXXX");
		blockData.put("MIR.SessionNumber", "6156");
		blockData.put("MIR.SequenceNumber", "391667");
		blockData.put("xml", expectedXmlApplicationHeader(blockData));
		validBlocks.add(blockData);

		// Output Block 2
		blockData = new HashMap<String, String>();
		blockData.put("rawContent", "{2:O5640004210831MYBANKTUBBRA99001234560101012000N}");
		blockData.put("BlockIdentifier", "2");
		blockData.put("InOutID", "O");
		blockData.put("MT", "564");
		blockData.put("InputTime", "0004");
		blockData.put("MIR", "210831MYBANKTUBBRA9900123456");
		blockData.put("OutputDate", "010101");
		blockData.put("OutputTime", "2000");
		blockData.put("Priority", "N");
		blockData.put("MIR.SendersDate", "210831");
		blockData.put("MIR.LogicalTerminal", "MYBANKTUBBRA");
		blockData.put("MIR.SessionNumber", "9900");
		blockData.put("MIR.SequenceNumber", "123456");
		blockData.put("xml", expectedXmlApplicationHeader(blockData));
		validBlocks.add(blockData);

		return validBlocks;
	}

	public static ArrayList<String> getInvalidTextBlockTestCases() {
		ArrayList<String> invalidBlocks = new ArrayList<String>();

		invalidBlocks.add(":20C:MISSing brackets");
		invalidBlocks.add("{");
		invalidBlocks.add("}");
		invalidBlocks.add("{5:\n" + ":20C:ABC" + "\n-}");
		invalidBlocks.add("{4:" + ":20C:ABC" + "\n-}");
		invalidBlocks.add("{4\n:" + ":20C:ABC" + "\n}");
		invalidBlocks.add("{4\n:" + ":20C:ABC" + "-}");
		invalidBlocks.add("{4\n:" + ":20C:ABC" + "\n-");

		return invalidBlocks;
	}

	public static ArrayList<String[]> getValidTextBlockTestCases() throws FileNotFoundException {
		ArrayList<String[]> validBlocks = new ArrayList<String[]>();
		ArrayList<String[]> validBlockFiles = new ArrayList<String[]>();

		for (int i = 1; i <= 8; i++) {
			validBlockFiles.add(new String[] { "textblock" + i + ".txt", "textblock" + i + ".xml" });
		}

		for (String[] testCase : validBlockFiles) {
			String blockContentFile = validTextBlockFolder + testCase[0];
			String ctrlXmlFile = validTextBlockFolder + testCase[1];

			String blockContent = "{4:\n" + TestingUtils.readFileContent(blockContentFile) + "\n-}";
			String ctrlXmlContent = TestingUtils.readFileContent(ctrlXmlFile);

			validBlocks.add(new String[] { blockContent, ctrlXmlContent });
		}

		return validBlocks;
	}

	public static ArrayList<Map<String, String>> getValidBasicHeaderBlockTestCases() throws Exception {
		ArrayList<Map<String, String>> validBlocks = new ArrayList<Map<String, String>>();
		Map<String, String> blockData;

		// Test block 1
		blockData = new HashMap<String, String>();
		blockData.put("rawContent", "{1:F01MYBABBICAXXX0878450607}");
		blockData.put("BlockIdentifier", "1");
		blockData.put("AppID", "F");
		blockData.put("ServiceID", "01");
		blockData.put("LTAddress", "MYBABBICAXXX");
		blockData.put("LTAddress.BIC", "MYBABBICXXX");
		blockData.put("LTAddress.LogicalTerminal", "A");
		blockData.put("LTAddress.BIC8", "MYBABBIC");
		blockData.put("SessionNumber", "0878");
		blockData.put("SequenceNumber", "450607");
		blockData.put("xml", expectedXmlBasicHeader(blockData));
		validBlocks.add(blockData);

		// Test block 2
		blockData = new HashMap<String, String>();
		blockData.put("rawContent", "{1:F01SOMEBIKKABRA1234995511}");
		blockData.put("BlockIdentifier", "1");
		blockData.put("AppID", "F");
		blockData.put("ServiceID", "01");
		blockData.put("LTAddress", "SOMEBIKKABRA");
		blockData.put("LTAddress.BIC", "SOMEBIKKBRA");
		blockData.put("LTAddress.LogicalTerminal", "A");
		blockData.put("LTAddress.BIC8", "SOMEBIKK");
		blockData.put("SessionNumber", "1234");
		blockData.put("SequenceNumber", "995511");
		blockData.put("xml", expectedXmlBasicHeader(blockData));
		validBlocks.add(blockData);

		return validBlocks;
	}

	private static String expectedXmlBasicHeader(Map<String, String> blockData) {
		StringBuilder sb = new StringBuilder();

		sb.append(XmlFactory.openNode("BasicHeader"));
		sb.append(XmlFactory.writeNode("ApplicationIdentifier", blockData.get("AppID")));
		sb.append(XmlFactory.writeNode("ServiceIdentifier", blockData.get("ServiceID")));
		sb.append(XmlFactory.writeNode("LTAddress", blockData.get("LTAddress")));

		sb.append(XmlFactory.openNode("LTAddress_Details"));
		sb.append(XmlFactory.writeNode("BIC", blockData.get("LTAddress.BIC")));
		sb.append(XmlFactory.writeNode("LogicalTerminal", blockData.get("LTAddress.LogicalTerminal")));
		sb.append(XmlFactory.writeNode("BIC8", blockData.get("LTAddress.BIC8")));
		sb.append(XmlFactory.closeNode("LTAddress_Details"));

		sb.append(XmlFactory.writeNode("SessionNumber", blockData.get("SessionNumber")));
		sb.append(XmlFactory.writeNode("SequenceNumber", blockData.get("SequenceNumber")));

		sb.append(XmlFactory.closeNode("BasicHeader"));

		return sb.toString();
	}

	private static String expectedXmlApplicationHeader(Map<String, String> blockData) throws Exception {
		StringBuilder sb = new StringBuilder();

		sb.append(XmlFactory.openNode("ApplicationHeader"));
		sb.append(XmlFactory.writeNode("InputOutputIdentifier", blockData.get("InOutID")));
		sb.append(XmlFactory.writeNode("MessageType", blockData.get("MT")));

		if (blockData.get("InOutID").equals("I")) {
			sb.append(XmlFactory.writeNode("DestAddress", blockData.get("DestAddress")));

			sb.append(XmlFactory.openNode("DestAddress_Details"));
			sb.append(XmlFactory.writeNode("BIC", blockData.get("DestAddress.BIC")));
			sb.append(XmlFactory.writeNode("LogicalTerminal", blockData.get("DestAddress.LogicalTerminal")));
			sb.append(XmlFactory.writeNode("BIC8", blockData.get("DestAddress.BIC8")));
			sb.append(XmlFactory.closeNode("DestAddress_Details"));

			sb.append(XmlFactory.writeNode("Priority", blockData.get("Priority")));
			sb.append(XmlFactory.writeNode("DeliveryMonitoring", blockData.get("DeliveryMonitoring")));
			sb.append(XmlFactory.writeNode("ObsolencePeriod", blockData.get("ObsolencePeriod")));

		} else if (blockData.get("InOutID").equals("O")) {
			sb.append(XmlFactory.writeNode("InputTime", blockData.get("InputTime")));
			sb.append(XmlFactory.writeNode("MIR", blockData.get("MIR")));

			sb.append(XmlFactory.openNode("MIR_Details"));
			sb.append(XmlFactory.writeNode("SendersDate", blockData.get("MIR.SendersDate")));
			sb.append(XmlFactory.writeNode("LogicalTerminal", blockData.get("MIR.LogicalTerminal")));
			sb.append(XmlFactory.writeNode("SessionNumber", blockData.get("MIR.SessionNumber")));
			sb.append(XmlFactory.writeNode("SequenceNumber", blockData.get("MIR.SequenceNumber")));
			sb.append(XmlFactory.closeNode("MIR_Details"));

			sb.append(XmlFactory.writeNode("OutputDate", blockData.get("OutputDate")));
			sb.append(XmlFactory.writeNode("OutputTime", blockData.get("OutputTime")));
			sb.append(XmlFactory.writeNode("Priority", blockData.get("Priority")));
		} else
			throw new Exception("Unable to identify InOutID when constructing expected XML");

		sb.append(XmlFactory.closeNode("ApplicationHeader"));

		return sb.toString();
	}

	public static ArrayList<Map<String, String>> getValidTags() throws IOException {
		ArrayList<Map<String, String>> validTags = new ArrayList<Map<String, String>>();

		CsvReaderBuilder builder = CsvReader.builder().fieldSeparator(';');
		CsvReader reader = builder.build(new File(validTagFile).toPath(), Charset.defaultCharset());

		CsvReaderBuilder fieldBuilder = CsvReader.builder().fieldSeparator('|');

		for (CsvRow row : reader) {
			String rawData = row.getField(0);
			String tag = row.getField(1).split("Field")[1];
			String fieldValues = row.getField(2);
			CsvReader fieldReader = fieldBuilder.build(fieldValues);

			Map<String, String> tagData = new LinkedHashMap<String, String>();
			tagData.put("RawContent", rawData.substring(tag.length() + 2));
			tagData.put("Tag", tag);
			for (CsvRow fieldRow : fieldReader) {

				// Get values of subfields
				for (String valuePair : fieldRow.getFields()) {
					String fieldName = valuePair.substring(0, valuePair.indexOf('='));
					String fieldValue = valuePair.substring(valuePair.indexOf('=') + 1);
					tagData.put(fieldName, fieldValue);
				}
				validTags.add(tagData);
			}
		}

		return validTags;
	}

	private static Map<String, ArrayList<String[]>> buildTestcasesFromCSV(String folder, String[] validBlocksInputs,
			String blockIdentifier) throws IOException {
		Map<String, ArrayList<String[]>> validBlocks = new HashMap<String, ArrayList<String[]>>();
		// Read blockdata from csv files
		for (String testcase : validBlocksInputs) {
			String filepath = folder + testcase;
			createTestCaseFromCsv(validBlocks, filepath, blockIdentifier);

		}
		return validBlocks;
	}

	private static void createTestCaseFromCsv(Map<String, ArrayList<String[]>> validBlocks, String filepath,
			String blockIdentifier) throws IOException {
		CsvReaderBuilder builder = CsvReader.builder().commentStrategy(CommentStrategy.SKIP);
		CsvReader csv = builder.build(new File(filepath).toPath(), Charset.defaultCharset());

		String block = "";
		ArrayList<String[]> blockTags = new ArrayList<String[]>();
		for (CsvRow row : csv) {
			String[] tagPair = row.getFields().stream().toArray(String[]::new);

			blockTags.add(tagPair);
			block += String.format("{%s:%s}", tagPair[0], tagPair[1]);
		}
		block = String.format("{" + blockIdentifier + ":%s}", block);

		validBlocks.put(block, blockTags);
	}

}
