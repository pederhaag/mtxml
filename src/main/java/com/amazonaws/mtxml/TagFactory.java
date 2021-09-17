package com.amazonaws.mtxml;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.siegmar.fastcsv.reader.CommentStrategy;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import de.siegmar.fastcsv.reader.CsvReader.CsvReaderBuilder;

public class TagFactory {
	private final static String resourcesPath = new File("src/main/resources").getAbsolutePath();
	private final static String tagDefinitionsFilePath = resourcesPath + "/tagDefinitions.txt";

	private static Map<String, String> charsetsPatterns = new HashMap<String, String>();
	private final static String fieldDescriptionStructure = "\\((\\w+)\\)";

	private static Map<String, ArrayList<String>> tagFields = new HashMap<String, ArrayList<String>>();
	public static Map<String, String> tagFieldsCharsets = new HashMap<String, String>();
	private static Map<String, String> tagRegex = new HashMap<String, String>();

	public TagFactory() throws IOException {
		initCharsets();
		try {
			loadTagDefinitions();
		} catch (Exception e) {
			e.printStackTrace();
		}

		int sink = 1;
//		buildFieldRegex(":4!c/[8c]/30x", "(Qualifier)(DataSourceScheme)(InstrumentCodeOrDescription)");
	}

	private void initCharsets() {
		charsetsPatterns.put("n", "\\d");
		charsetsPatterns.put("a", "[A-Z]");
		charsetsPatterns.put("c", "[A-Z0-9]");
		charsetsPatterns.put("h", "[A-F0-9]");
		charsetsPatterns.put("d", "[0-9,]");

		// SWIFT Charactersets
		charsetsPatterns.put("x", "[a-zA-Z0-9\\/\\-?:().,'+ ]");
//		charsetsPatterns.put("x", "[a-zA-Z0-9\\/\\-?:().,'+ \\n]");
		charsetsPatterns.put("y", "[A-Z0-9\\/\\-?:().,'+=!\\\"%&*<>; ]");
		charsetsPatterns.put("z", "[a-zA-Z0-9\\/\\-?:().,'+=!\\\"%&*<>;{@#_ \\r\\n]");

	}

	private void loadTagDefinitions() throws Exception {

		CsvReaderBuilder builder = CsvReader.builder().commentStrategy(CommentStrategy.SKIP).fieldSeparator('\t');
		CsvReader reader = builder.build(new File(tagDefinitionsFilePath).toPath(), Charset.defaultCharset());

		Pattern fieldDescriptionPattern = Pattern.compile(fieldDescriptionStructure, Pattern.MULTILINE);
		Matcher fieldDescriptionMatcher;

		Pattern tagInfoPattern = Pattern.compile(
				"(?<BracketPrefix>\\[)?(?<Prefix>(?>[/A-Z :,])*)(?>(?<NewLine>\\\\n)|(?<UTCInd>\\[N\\]2!n\\[2!n\\])|(?<Length>\\d+(?>\\-\\d+|!|\\*\\d+)?)(?<Charset>n|a|h|x|y|z|c|d)|(?<Static>[^\\[\\]]))(?<Suffix>/*)(?<BracketSuffix>\\])?");

		Matcher tagInfoMatcher;

		for (CsvRow row : reader) {
			String tag = row.getField(0);
			String format = row.getField(1);
			String formatinfo = row.getField(2);

			tagInfoMatcher = tagInfoPattern.matcher(format);
			fieldDescriptionMatcher = fieldDescriptionPattern.matcher(formatinfo);
			String tagRegx = "";
			ArrayList<String> fieldList = new ArrayList<String>();
			String largerOptionalGrpRegex = "";

			while (tagInfoMatcher.find()) {
				String fieldName = null;
				if (tagInfoMatcher.group("NewLine") == null) {
					fieldDescriptionMatcher.find();
					fieldName = fieldDescriptionMatcher.group(1);
					fieldList.add(fieldName);
					tagFieldsCharsets.put(tag + ":" + fieldName, tagInfoMatcher.group("Charset"));
				}

				String LeftBracket = tagInfoMatcher.group("BracketPrefix");
				String RightBracket = tagInfoMatcher.group("BracketSuffix");
				String subfieldRegex = createFieldRegex(tagInfoMatcher, fieldName);
				if (LeftBracket != null && RightBracket != null) {
					// Individual optional tag
					tagRegx += String.format("(?>%s)?", subfieldRegex);

				} else if (LeftBracket != null && RightBracket == null) {
					// Start of larger optional group
					largerOptionalGrpRegex = subfieldRegex;

				} else if (LeftBracket == null && RightBracket != null) {
					// End of larger optional group
					largerOptionalGrpRegex += subfieldRegex;
					tagRegx += String.format("(?>%s)?", largerOptionalGrpRegex);
					largerOptionalGrpRegex = "";
				} else if (LeftBracket == null && RightBracket == null)
					if (largerOptionalGrpRegex.length() > 0) {
						// Middle of larger optional group
						largerOptionalGrpRegex += subfieldRegex;
					} else {
						// Single non-optional field
						tagRegx += subfieldRegex;
					}

			}
			tagRegex.put(tag, tagRegx);
			tagFields.put(tag, fieldList);

		}

	}

	public static String getRegexQuantifier(String desc) throws Exception {
		int i = desc.indexOf('-');
		if (i > 0) {
			String from = desc.substring(0, i);
			String to = desc.substring(i + 1, desc.length());
			return String.format("{%s,%s}", from, to);
		}
		i = desc.indexOf('*');
		if (i > 0) {
			throw new Exception("getRegexQuantifier should not be called with '*' as argument.");
		}
		i = desc.indexOf('!');
		if (i > 0) {
			return String.format("{%s}", desc.substring(0, desc.length() - 1));
		}

		return String.format("{%s,%s}", 0, desc);
	}

	public static String createFieldRegex(Matcher tagInfoMatcher, String fieldName) throws Exception {
		String Prefix = regexExcape(tagInfoMatcher.group("Prefix"));
		String length = tagInfoMatcher.group("Length");
		String Charset = tagInfoMatcher.group("Charset");
		String Suffix = regexExcape(tagInfoMatcher.group("Suffix"));
		String staticField = regexExcape(tagInfoMatcher.group("Static"));
		String UTCInd = tagInfoMatcher.group("UTCInd");
		String newLine = tagInfoMatcher.group("NewLine");

		String fieldName32;
		if (fieldName != null) {
			fieldName32 = fieldName.length() > 32 ? fieldName.substring(0, 32) : fieldName;
		} else {
			fieldName32 = fieldName;
		}

		String regex = null;
		String fieldRegex = null;

		if (staticField != null) {
			if (fieldName.equals("Sign")) {
				fieldRegex = String.format("(?<%s>(?>%s))", fieldName, staticField);
			} else {
				fieldRegex = String.format("(?>%s)", staticField);
			}

		} else if (newLine != null) {
			regex = "\\n";

		} else if (UTCInd != null) {
			fieldRegex = String.format("(?<%s>(?>N?(?>%s{2}){1,2}))", fieldName32, charsetsPatterns.get("n"));

		} else if (length.contains("*")) {
			String characterSet = charsetsPatterns.get(Charset).replace("\\n", "");
			int numLines = Integer.valueOf(length.split("\\*")[0]);
			String lineLength = length.split("\\*")[1];

			if (tagInfoMatcher.start() > 0) {
				// If there is a multiline which is not the first field, then it needs to be
				// starting on a new line
				fieldRegex = String.format("\\n?(?<%s>(?>%s{0,%s})(?>\\n%s{0,%s}){0,%d})", fieldName32, characterSet,
						lineLength, characterSet, lineLength, numLines - 1);
			} else {

				fieldRegex = String.format("(?<%s>(?>%s{0,%s}(?>\\n%s{0,%s}){0,%d}))", fieldName32, characterSet,
						lineLength, characterSet, lineLength, numLines - 1);
			}
		} else {
			String charsetRegex = charsetsPatterns.get(Charset);
			String lengthRegex = getRegexQuantifier(length);
			fieldRegex = String.format("(?<%s>%s%s)", fieldName32, charsetRegex, lengthRegex);
		}

		if (regex == null) {
			regex = String.format("%s%s%s", Prefix, fieldRegex, Suffix);
		}

		return regex;
	}

	private static String regexExcape(String regex) {
		if (regex != null) {

			StringBuilder sb = new StringBuilder();
			char c;
			for (int i = 0; i < regex.length(); i++) {
				c = regex.charAt(i);
				if (c == '/') {
					sb.append("\\/");

				} else
					sb.append(c);
			}
			return sb.toString();
		}
		return regex;

	}

	public Tag createTag(String tag, String content) throws UnknownTagException, MTException {
		ArrayList<String> fieldNames = getTagFieldNames(tag);
		String tagContentRegex = getTagRegex(tag);
		Pattern tagContentPattern = Pattern.compile(tagContentRegex, Pattern.MULTILINE);
		Matcher tagContentMatcher = tagContentPattern.matcher(content);
		ArrayList<String> fieldValues = new ArrayList<String>();

		if (!tagContentMatcher.find()) {
			throw new SyntaxException(tagContentMatcher.pattern().toString(), tag);
		}
		if (tagContentMatcher.start() > 0)
			throw new SyntaxException(tagContentMatcher.pattern().toString(), tag);

		String fieldValue;
		for (String fieldName : fieldNames) {
			fieldValue = tagContentMatcher.group(fieldName);
			if (fieldValue == null)
				fieldValue = "";

			if (fieldName.equals("Amount"))
				validateAmountField(fieldValue);

			fieldValues.add(fieldValue);
//			System.out.println(fieldName + " = " + fieldValue);

		}
		int noOverrunChars = content.length() - tagContentMatcher.end() - (tag.length() + 2);
		if (noOverrunChars > 0) {
			System.out.println(String.format(
					"[Warning] FieldOverrunError: An additional %d characters could not be successfully be treated as part of any subfield in tag %s.",
					noOverrunChars, tag));
		}

//		if (fieldNames.get(0).equals("Qualifier")) {
//			String qualifier = fieldValues.remove(0);
//			fieldNames.remove(0);
//			return new Tag(tag, fieldNames, fieldValues, qualifier);
//		} else {
//			return new Tag(tag, fieldNames, fieldValues);
//		}
		return new Tag(tag, fieldNames, fieldValues);

	}

	private void validateAmountField(String value) throws SyntaxException {
		String error = null;
		int commaIx = value.indexOf(',');
		if (commaIx == -1) {
			error = "Malformed amount field: Lacking comma";
		} else if (commaIx == 0) {
			error = "Malformed amount field: Lacking digits ahead of comma";
		} else if (value.indexOf(',', commaIx + 1) > -1) {
			error = "Malformed amount field: Multiple commas in field";
		}
		if (error != null) {
			throw new SyntaxException(error);
		}
	}

	private ArrayList<String> getTagFieldNames(String tag) throws UnknownTagException {
		if (tagFields.containsKey(tag)) {
			return tagFields.get(tag);
		}
		throw new UnknownTagException(tag);
	}

	String getTagRegex(String tag) throws UnknownTagException {
		if (tagRegex.containsKey(tag)) {
			return tagRegex.get(tag);
		}
		throw new UnknownTagException(tag);
	}

	public static void main(String[] args) throws IOException, UnknownTagException, MTException {
		TagFactory tf = new TagFactory();
		System.out.println(tf.getTagRegex("50K"));
		String content = "/NV4906448882251\\nBajjwstoxlmcnuti\\nJauxqn BIIs wget 0\\nOpssgzns 2732 bizg\\n0321 Hsxd".replace("\\n", "\n");
		System.out.println(content);
//		System.out.println(tf.createTag("11A", ":TANH//STK").getFieldValue("Qualifier"));
		System.out.println(tf.createTag("50K", content));

	}

}
