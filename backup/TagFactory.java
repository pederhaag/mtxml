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
		charsetsPatterns.put("d", "[0-9,]"); // Needs special care in order to consider decimals nnnnn,nn and decimals
											// type see 98H specs

		// SWIFT Charactersets
		charsetsPatterns.put("x", "[a-zA-Z0-9\\/\\-?:().,'+ ]");
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
//		Pattern tagInfoPattern = Pattern.compile(
//				"(?<BracketPrefix>\\[)?(?<Prefix>(?>[/A-Z :,])*)(?>(?<UTCInd>\\[N\\]2!n\\[2!n\\])|(?<Length>\\d+(?>\\-\\d+|!|\\*\\d+)?)(?<Charset>n|a|h|x|y|z|c|d)|(?<Static>[^\\[\\]]))(?<Suffix>/*)(?<BracketSuffix>\\])?");

		Matcher tagInfoMatcher;

		System.out.println("TagInfoPattern: " + tagInfoPattern);

		for (CsvRow row : reader) {
			String tag = row.getField(0);
			String format = row.getField(1);
			String formatinfo = row.getField(2);

			if (tag.equals("33B") || 1 < 2) {
				tagInfoMatcher = tagInfoPattern.matcher(format);
				fieldDescriptionMatcher = fieldDescriptionPattern.matcher(formatinfo);
				String fieldRegex = "";
				ArrayList<String> fieldList = new ArrayList<String>();

				while (tagInfoMatcher.find()) {
					String fieldName = null;
					if (tagInfoMatcher.group("NewLine") == null) {
						fieldDescriptionMatcher.find();
						fieldName = fieldDescriptionMatcher.group(1);
						fieldList.add(fieldName);
						tagFieldsCharsets.put(tag + ":" + fieldName, tagInfoMatcher.group("Charset"));
					}
					fieldRegex += createFieldRegex(tagInfoMatcher, fieldName);

				}
				tagRegex.put(tag, fieldRegex);
				tagFields.put(tag, fieldList);

//				System.out.println(tag + " ==> " + format + " --> " + formatInfo + "\n" + "REGEX:" + fieldRegex + "\n");
				System.out.println(tag + " ==> " + format + "\n" + "REGEX:" + fieldRegex + "\n");

			}
		}

	}

	public static String getLengthRegex(String desc) throws Exception {
		int i = desc.indexOf('-');
		if (i > 0) {
			String from = desc.substring(0, i);
			String to = desc.substring(i + 1, desc.length());
			return String.format("{%s,%s}", from, to);
		}
		i = desc.indexOf('*');
		if (i > 0) {
			throw new Exception("getLengthRegex should not be called with '*' as argument.");
		}
		i = desc.indexOf('!');
		if (i > 0) {
			return String.format("{%s}", desc.substring(0, desc.length() - 1));
		}

		return String.format("{%s,%s}", 0, desc);
	}

	public static String getCharsetRegex(String charset) {
		return charsetsPatterns.get(charset.charAt(0));
	}

	public static String createFieldRegex(Matcher tagInfoMatcher, String fieldName) throws Exception {
		String BracketPrefix = tagInfoMatcher.group("BracketPrefix");
		String Prefix = regexExcape(tagInfoMatcher.group("Prefix"));
		String length = tagInfoMatcher.group("Length");
		String Charset = tagInfoMatcher.group("Charset");
		String Suffix = regexExcape(tagInfoMatcher.group("Suffix"));
		String BracketSuffix = tagInfoMatcher.group("BracketSuffix");
		String staticField = regexExcape(tagInfoMatcher.group("Static"));
		String UTCInd = tagInfoMatcher.group("UTCInd");
		String newLine = tagInfoMatcher.group("NewLine");
		Boolean optional = BracketPrefix != null;

		String regex = null;
		String fieldRegex = null;

		if (staticField != null) {
			fieldRegex = String.format("(?>%s)", staticField);

		} else if (newLine != null) {
			regex = "\\n";

		} else if (UTCInd != null) {
			fieldRegex = String.format("(?<%s>(?>N?(?>%s{2}){1,2}))", fieldName, charsetsPatterns.get("n"));

		} else if (length.contains("*")) {
			String characterSet = charsetsPatterns.get(Charset);
			int numLines = Integer.valueOf(length.split("\\*")[0]);
			String lineLength = length.split("\\*")[1];
			fieldRegex = String.format("(?<%s>(?>%s{0,%s}(?>\\n%s{0,%s}){0,%d}))", fieldName, characterSet, lineLength,
					characterSet, lineLength, numLines - 1);
		} else {
			String charsetRegex = charsetsPatterns.get(Charset);
			String lengthRegex = getLengthRegex(length);
			fieldRegex = String.format("(?<%s>%s%s)", fieldName, charsetRegex, lengthRegex);
		}

		if (regex == null) {
			regex = String.format("(?>%s%s%s)%s", Prefix, fieldRegex, Suffix, optional ? "?" : "");
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
//				} else if (c == '\\') {
//					sb.append("\\");
//					sb.append("\\");
				} else
					sb.append(c);
			}
			return sb.toString();
		}
		return regex;

	}

	public static String createFieldRegexStatic(String fieldSpecs, String fieldName) {
		Boolean optional = fieldSpecs.charAt(0) == '[';
		String value = optional ? fieldSpecs.substring(1, fieldSpecs.length() - 1) : fieldSpecs;
		return String.format("(?<%s>(?>%s)%s)", fieldName, value, optional ? "?" : "");
	}

	public Tag createTag() {
		return null;
	}

	public static void main(String[] args) throws IOException {
		TagFactory tf = new TagFactory();
		int sink = 1+1;
		// TODO: Videre.. sjekk at ting fungerer hvor multiline strings kombineres med
		// andre ting f.eks i tag 95V
		// .... ser ut som det gaar noe feil der, men i Qualifier felt, ikke
		// NameAndAddress.. interessant

		// Deretter maa jeg jobbe med <br /> feltene.... Usikker paa hva fremgangsmaaten
		// blir der (f.eks tag 88D)

//		System.out.println(createFieldRegex("4!", "c", "Qualifier"));

//		System.out.println(createFieldRegexStatic("N", "Sign"));
//		System.out.println(createFieldRegexStatic("[ASD]", "Sign"));
//		System.out.println(createFieldRegexStatic("ASD", "Sign"));
	}

}
