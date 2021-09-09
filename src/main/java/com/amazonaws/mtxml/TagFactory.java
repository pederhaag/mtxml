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
	private final static String[] charsets = { "n", "a", "h", "d", "x", "y", "z", };
	private final static String tagStructure = "(?<Tag>\\d\\d)(?<Qualifier>[A-Z]?)";
	private final static String fieldDescriptionStructure = "\\((\\w+)\\)";

	private static Map<String, ArrayList<String>> Tags = new HashMap<String, ArrayList<String>>();

	public TagFactory() throws IOException {
		initCharsets();
		loadTagDefinitions();

//		buildFieldRegex(":4!c/[8c]/30x", "(Qualifier)(DataSourceScheme)(InstrumentCodeOrDescription)");
	}

	private void initCharsets() {
		charsetsPatterns.put("n", "\\d");
		charsetsPatterns.put("a", "[A-Z]");
		charsetsPatterns.put("h", "[A-F0-9]");
		charsetsPatterns.put("d", "[0-9]"); // Needs special care in order to consider decimals nnnnn,nn and decimals
											// type see 98H specs

		// SWIFT Charactersets
		charsetsPatterns.put("x", "[a-zA-Z0-9\\/\\-?:().,'+ ]");
		charsetsPatterns.put("y", "[A-Z0-9\\/\\-?:().,'+=!\\\"%&*<>; ]");
		charsetsPatterns.put("z", "[a-zA-Z0-9\\/\\-?:().,'+=!\\\"%&*<>;{@#_ \\r\\n]");

	}

	private void buildFieldRegex(String fieldSpec, String subfieldDescriptions) {

		Pattern subFieldNamesPatterns = Pattern.compile(fieldDescriptionStructure);
		Matcher subFieldNamesMatcher = subFieldNamesPatterns.matcher(subfieldDescriptions);

		while (subFieldNamesMatcher.find()) {
			String tagString = subFieldNamesMatcher.group(0);
			String tag = subFieldNamesMatcher.group(1);
			String value = subFieldNamesMatcher.group(2);

			// TODO: Her er jeg. Paa tide aa lage tags/blocks
		}

	}

	private void loadTagDefinitions() throws IOException {

		CsvReaderBuilder builder = CsvReader.builder().commentStrategy(CommentStrategy.SKIP).fieldSeparator('\t');
		CsvReader reader = builder.build(new File(tagDefinitionsFilePath).toPath(), Charset.defaultCharset());

		Pattern fieldDescriptionPattern = Pattern.compile(fieldDescriptionStructure, Pattern.MULTILINE);
		Matcher fieldDescriptionMatcher;

		Pattern tagInfoPattern = Pattern.compile(
				"(?<BracketPrefix>\\[)?(?<Prefix>(?>[/A-Z :,])*)(?>(?<UTCInd>\\[N\\]2!n\\[2!n\\])|(?<Length>\\d+(?>\\-\\d+|!|\\*\\d+)?)(?<Charset>n|a|h|x|y|z|c|d)|(?<Static>[^\\[\\]]))(?<Suffix>/*)(?<BracketSuffix>\\])?");
//		Pattern tagInfoPattern = Pattern
//				.compile("(?>(?<Length>\\d+(?>\\-\\d+|!|\\*\\d+)?)(?<Charset>n|a|h|x|y|z|c|d)|(?<Static>\\[.\\]))");
		Matcher tagInfoMatcher;

		for (CsvRow row : reader) {
			String tag = row.getField(0);
			String format = row.getField(1);
			String formatinfo = row.getField(2);

			if (tag.equals("89D") || 2 < 2) {
				String formatInfo = "\n";

				tagInfoMatcher = tagInfoPattern.matcher(format);
				fieldDescriptionMatcher = fieldDescriptionPattern.matcher(formatinfo);
				String fieldRegex = "";
				ArrayList<String> fieldList = new ArrayList<String>();

				while (tagInfoMatcher.find()) {
					fieldDescriptionMatcher.find();
					String fieldName = fieldDescriptionMatcher.group(1);
					fieldList.add(fieldName);

					fieldRegex += createFieldRegex(tagInfoMatcher, fieldName);
//					System.out.println(createFieldRegex(tagInfoMatcher, fieldName));

//					String BracketPrefix = tagInfoMatcher.group("BracketPrefix");
//					String Prefix = tagInfoMatcher.group("Prefix");
//					String length = tagInfoMatcher.group("Length");
//					String Charset = tagInfoMatcher.group("Charset");
//					String Suffix = tagInfoMatcher.group("Suffix");
//					String BracketSuffix = tagInfoMatcher.group("BracketSuffix");
//					String staticField = tagInfoMatcher.group("Static");
//					String UTCInd = tagInfoMatcher.group("UTCInd");
//					Boolean optional = BracketPrefix != null;
//
//					if (tagInfoMatcher.group("Static") != null) {
//						fieldRegex = fieldRegex + createFieldRegexStatic(staticField, fieldDescription);
//						formatInfo = formatInfo + "\t"
//								+ String.format("%s:\tStaticValue=%s", fieldDescription, staticField);
//
//					} else if (tagInfoMatcher.group("UTCInd") != null) {
//						formatInfo = formatInfo + "\t" + String.format("%s:\tUTCInd=%s", fieldDescription, staticField);
//
//					} else {
//						String charset = tagInfoMatcher.group("Charset");
//						formatInfo = formatInfo + "\t"
//								+ String.format("%s:\tLength=%s, charset=%s", fieldDescription, length, charset);
//					}

				}
				Tags.put(tag, fieldList);

//				System.out.println(tag + " ==> " + format + " --> " + formatInfo + "\n" + "REGEX:" + fieldRegex + "\n");
				System.out.println(tag + " ==> " + format + "\n" + "REGEX:" + fieldRegex + "\n");

			}
		}

	}

	public static String getLengthRegex(String desc) {
		int i = desc.indexOf('-');
		if (i > 0) {
			String from = desc.substring(0, i);
			String to = desc.substring(i + 1, desc.length());
			return String.format("{%s,%s}", from, to);
		}
		i = desc.indexOf('*');
		if (i > 0) {
//			String numLines = desc.substring(0, i);
//			String lineLength = desc.substring(i + 1, desc.length());
//			return String.format("{%s,%s}", 0, lineLength);
		}
		i = desc.indexOf('!');
		if (i > 0) {
			return String.format("{%s,%s}", 0, desc.substring(0, desc.length() - 1));
		}

		return String.format("{%s,%s}", 0, desc);
	}

	public static String getCharsetRegex(String charset) {
		return charsetsPatterns.get(charset.charAt(0));
	}

	public static String createFieldRegex(Matcher tagInfoMatcher, String fieldName) {
		String BracketPrefix = tagInfoMatcher.group("BracketPrefix");
		String Prefix = tagInfoMatcher.group("Prefix");
		String length = tagInfoMatcher.group("Length");
		String Charset = tagInfoMatcher.group("Charset");
		String Suffix = tagInfoMatcher.group("Suffix");
		String BracketSuffix = tagInfoMatcher.group("BracketSuffix");
		String staticField = tagInfoMatcher.group("Static");
		String UTCInd = tagInfoMatcher.group("UTCInd");
		Boolean optional = BracketPrefix != null;

		String regex = "";
		String main = "";

		if (staticField != null) {
			main = String.format("(?>%s%s%s)%s", Prefix, staticField, Suffix, optional ? "?" : "");
		} else if (UTCInd != null) {
			main = String.format("(%sN?(?>%s{2}){1,2}%s)%s", Prefix, charsetsPatterns.get("n"), Suffix,
					optional ? "?" : "");
		} else if (length.contains("*")) {
			String characterSet = charsetsPatterns.get(Charset);
			int numLines = Integer.valueOf(length.split("\\*")[0]) ;
			String lineLength = length.split("\\*")[1];
			main = String.format("(?>%s{0,%s}(?>\\n%s{0,%s}){0,%d})", characterSet, lineLength, characterSet, lineLength, numLines - 1);
		} else {
			String charsetRegex = charsetsPatterns.get(Charset);
			String lengthRegex = getLengthRegex(length);
			main = String.format("(?>%s(?>%s)%s%s)%s", Prefix, charsetRegex, lengthRegex, Suffix, optional ? "?" : "");
		}

		if (main != null) {
			regex = String.format("(?<%s>%s)", fieldName, main);
		}

		return escape(regex);
	}

	private static String escape(String regex) {
		StringBuilder sb = new StringBuilder();
		char c;
		for (int i = 0; i < regex.length(); i++) {
			c = regex.charAt(i);
			if (c == '/') {
				sb.append("\\/");
			} else if (c == '\\') {
				sb.append("\\");
				sb.append("\\");
			} else
				sb.append(c);
		}
		return sb.toString();

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
		new TagFactory();
		// TODO: Videre.. sjekk at ting fungerer hvor multiline strings kombineres med andre ting f.eks i tag 95V
		// .... ser ut som det gaar noe feil der, men i Qualifier felt, ikke NameAndAddress.. interessant
		
		// Deretter maa jeg jobbe med <br /> feltene.... Usikker paa hva fremgangsmaaten blir der (f.eks tag 88D)
		
//		System.out.println(createFieldRegex("4!", "c", "Qualifier"));

//		System.out.println(createFieldRegexStatic("N", "Sign"));
//		System.out.println(createFieldRegexStatic("[ASD]", "Sign"));
//		System.out.println(createFieldRegexStatic("ASD", "Sign"));
	}

}
