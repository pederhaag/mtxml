package com.amazonaws.mtxml;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.siegmar.fastcsv.reader.CommentStrategy;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvReader.CsvReaderBuilder;
import de.siegmar.fastcsv.reader.CsvRow;

/**
 * 
 * {@code TagFactory} is a factory-class for creating {@code Tag}-objects from
 * raw MT-format.
 *
 */
public class TagFactory {
	/**
	 * Mapping for the different charactersets occurring in the MT-standard to
	 * regex-expressions
	 */
	private static final Map<String, String> charsetsPatterns;

	/**
	 * Regex for identifying the different field-names in the tag-definitions
	 */
	private final static String REGEX_FIELD_DESCRIPTION = "\\((\\w+)\\)";

	/**
	 * Mapping from tag (f. ex "19A") to an {@code ArrayList<String>} containing the
	 * fieldnames
	 */
	private static Map<String, ArrayList<String>> tagFields = new HashMap<String, ArrayList<String>>();

	/**
	 * Mapping of fieldnames to corresponding regex charactersets
	 */
	private static Map<String, String> tagFieldsCharsets = new HashMap<String, String>();

	/**
	 * Mapping of tag to regex-representation of the tag
	 */
	private static Map<String, String> tagRegex = new HashMap<String, String>();

	/**
	 * This regex parses identifies the different components of MT-standard format
	 * of a tag
	 */
//	private static final String REGEX_TAG_INFO = "(?<BracketPrefix>\\[)?(?<Prefix>(?>[/A-Z :,])*)(?>(?<NewLine>\\\\n)|(?<UTCInd>\\[N\\]2!n\\[2!n\\])|(?<Length>\\d+(?>\\-\\d+|!|\\*\\d+)?)(?<Charset>n|a|h|x|y|z|c|d)|(?<Static>[^\\[\\]]))(?<Suffix>/*)(?<BracketSuffix>\\])?";
	private static final String REGEX_TAG_INFO = "(?<BracketPrefix>\\[)?(?<Prefix>(?>[/A-Z :,])*)(?>(?<NewLine>\\\\n)|(?<UTCInd>\\[N\\]2!n\\[2!n\\])|(?<Length>\\d+(?>\\-\\d+|!|\\*\\d+)?)(?<Charset>n|a|h|x|y|z|c|d)|(?<Static>[^\\[\\]]))(?<Suffix>/*)(?<BracketSuffix>\\])?";

	/**
	 * Static initialization
	 */
	static {
		Map<String, String> tempCharsetsPatterns = new HashMap<String, String>();
		tempCharsetsPatterns.put("n", "\\d");
		tempCharsetsPatterns.put("a", "[A-Z]");
		tempCharsetsPatterns.put("c", "[A-Z0-9]");
		tempCharsetsPatterns.put("h", "[A-F0-9]");
		tempCharsetsPatterns.put("d", "[0-9,]");

		// SWIFT Character-sets
		tempCharsetsPatterns.put("x", "[a-zA-Z0-9\\/\\-?:().,'+ ]");
		tempCharsetsPatterns.put("y", "[A-Z0-9\\/\\-?:().,'+=!\\\"%&*<>; ]");
		tempCharsetsPatterns.put("z", "[a-zA-Z0-9\\/\\-?:().,'+=!\\\"%&*<>;{@#_ \\r\\n]");
		charsetsPatterns = tempCharsetsPatterns;
	}

	public TagFactory() throws IOException {
		try {
			loadTagDefinitions();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Read the tag-definitions file and parse the MT-syntax to a regex-based format
	 * and populate the corresponding maps
	 * 
	 * @throws IOException If tag-definitions file cannot be found.
	 * @throws Exception   If there is an issure parsing a tag-definition
	 */
	private void loadTagDefinitions() throws IOException, Exception {
		// Objects for reading file
		CsvReaderBuilder builder = CsvReader.builder().commentStrategy(CommentStrategy.SKIP).fieldSeparator('\t');
//		CsvReader reader = builder.build(new File(TAG_DEFINITIONS_PATH).toPath(), Charset.defaultCharset());
		CsvReader reader = builder.build(Paths.get(this.getClass().getResource("/tagDefinitions.txt").toURI()),
				Charset.defaultCharset());

		// Tools for getting field-names
		Pattern fieldNamePattern = Pattern.compile(REGEX_FIELD_DESCRIPTION, Pattern.MULTILINE);
		Matcher fieldNameMatcher;

		// Tools for identifying tag-characteristics
		Pattern tagInfoPattern = Pattern.compile(REGEX_TAG_INFO);
		Matcher tagInfoMatcher;

		// Process each tag-definition
		for (CsvRow row : reader) {
			String tag = row.getField(0);
			String format = row.getField(1);
			String formatinfo = row.getField(2);

			tagInfoMatcher = tagInfoPattern.matcher(format);
			fieldNameMatcher = fieldNamePattern.matcher(formatinfo);

			// Parsing tools
			String tagRegx = "";
			ArrayList<String> fieldList = new ArrayList<String>();
			String largerOptionalGrpRegex = "";

			// While there exists more fields
			while (tagInfoMatcher.find()) {
				String fieldName = null;
				if (tagInfoMatcher.group("NewLine") == null) {
					// If we have come to an actual field (not just a single newline caught by the
					// regex) update the field-to-charactersets mapping
					fieldNameMatcher.find();
					fieldName = fieldNameMatcher.group(1);
					fieldList.add(fieldName);
					// Store the subfields character set
					tagFieldsCharsets.put(tag + ":" + fieldName, tagInfoMatcher.group("Charset"));
				}

				String LeftBracket = tagInfoMatcher.group("BracketPrefix");
				String RightBracket = tagInfoMatcher.group("BracketSuffix");
				String subfieldRegex = createFieldRegex(tagInfoMatcher, fieldName);
				// We need to handle fields differently based on it is optional and its
				// neighbours are also part of the same optional group
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

	/**
	 * Translate quantifiers used in MT-Standard to regex-versions
	 */
	private static String getRegexQuantifier(String desc) throws Exception {
		int i = desc.indexOf('-');
		if (i > 0) {
			String from = desc.substring(0, i);
			String to = desc.substring(i + 1, desc.length());
			return String.format("{%s,%s}", from, to);
		}
		i = desc.indexOf('*');
		if (i > 0) {
			// This multiline case is handled by itself in createFieldRegex-method
			throw new Exception("getRegexQuantifier should not be called with '*' as argument.");
		}
		i = desc.indexOf('!');
		if (i > 0) {
			return String.format("{%s}", desc.substring(0, desc.length() - 1));
		}

		return String.format("{%s,%s}", 0, desc);
	}

	/**
	 * This method takes the match result of a single field in a tag definiton and
	 * builds a regex for this field.
	 * 
	 * @param tagInfoMatcher {@code Matcher}-object which has matched the field
	 *                       definition
	 * @param fieldName      Name of the field
	 * @return A regex describing the field format, including length, optionality
	 *         and characterset
	 * @throws Exception
	 */
	private static String createFieldRegex(Matcher tagInfoMatcher, String fieldName) throws Exception {
		// Retrieve information from match result
		String Prefix = regexExcape(tagInfoMatcher.group("Prefix"));
		String length = tagInfoMatcher.group("Length");
		String Charset = tagInfoMatcher.group("Charset");
		String Suffix = regexExcape(tagInfoMatcher.group("Suffix"));
		String staticField = regexExcape(tagInfoMatcher.group("Static"));
		String UTCInd = tagInfoMatcher.group("UTCInd");
		String newLine = tagInfoMatcher.group("NewLine");

		// Regex groups only allow for groupname with a maximum length of 32 characters.
		// As of now, fields will be unique with respect to the first 32 characters so
		// we will use truncation if needed.
		String fieldName32;
		if (fieldName != null) {
			fieldName32 = fieldName.length() > 32 ? fieldName.substring(0, 32) : fieldName;
		} else {
			fieldName32 = fieldName;
		}

		// The string containing the final regex representing the field
		String regex = null;

		// Depending on the case, we will start by creating an inner part of the regex
		// and then create full regex later which also contains pre- and suffixes
		String subRegex = null;

		// Static field
		if (staticField != null) {
			if (fieldName.equals("Sign")) {
				subRegex = String.format("(?<%s>(?>%s))", fieldName, staticField);
			} else {
				subRegex = String.format("(?>%s)", staticField);
			}

		} else if (newLine != null) {
			// Simple newlines
			regex = "\\R";

		} else if (UTCInd != null) {
			// UTCIndicators have a predefined format. The complexity is best handled on its
			// own here
			subRegex = String.format("(?<%s>(?>N?(?>%s{2}){1,2}))", fieldName32, charsetsPatterns.get("n"));

		} else if (length.contains("*")) {
			// The case of multiline fields. Each line has a maximum length, and the maximum
			// number of lines is specified

			// If the characterset contains newlines, then we will remove it
			String characterSet = charsetsPatterns.get(Charset).replace("\\n", "");

			int numLines = Integer.valueOf(length.split("\\*")[0]);
			String lineLength = length.split("\\*")[1];

			if (tagInfoMatcher.start() > 0) {
				// If there is a multiline which is not the first field, then it needs to be
				// starting on a new line
				subRegex = String.format("\\R?(?<%s>(?>%s{0,%s})(?>\\R%s{0,%s}){0,%d})", fieldName32, characterSet,
						lineLength, characterSet, lineLength, numLines - 1);
			} else {

				subRegex = String.format("(?<%s>(?>%s{0,%s}(?>\\R%s{0,%s}){0,%d}))", fieldName32, characterSet,
						lineLength, characterSet, lineLength, numLines - 1);
			}
		} else {
			// Default case for a subfield
			String charsetRegex = charsetsPatterns.get(Charset);
			String lengthRegex = getRegexQuantifier(length);
			subRegex = String.format("(?<%s>%s%s)", fieldName32, charsetRegex, lengthRegex);
		}

		if (regex == null) {
			// Here we include the pre- and suffixes
			regex = String.format("%s%s%s", Prefix, subRegex, Suffix);
		}

		return regex;
	}

	/**
	 * Used to escape some characters not already handled elsewhere
	 */
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

	/**
	 * Factory-method for creating a tag. Using preloaded information about the tag
	 * definition
	 * 
	 * @param tag     Tag to create, i.e. "19A"
	 * @param content The contents of the field in the MT-message after the
	 *                ":{Tag}:"-prefix
	 * @return {@code Tag}-object representing the input.
	 * @throws UnknownTagException If the contents of the {@code tag} parameter does
	 *                             not represent a tag in the MT Standard
	 */
	public Tag createTag(String tag, String content) throws UnknownTagException {
		Objects.requireNonNull(tag, "Tag cannot be null");
		Objects.requireNonNull(content, "Tagcontent cannot be null");

		// Map for underlying subfields
		LinkedHashMap<String, MTComponent> subfields = new LinkedHashMap<String, MTComponent>();
		String qualifier = null;

		// Match the different subfields in the tag contents
		String tagContentRegex = getTagRegex(tag);
		Pattern tagContentPattern = Pattern.compile(tagContentRegex, Pattern.MULTILINE);
		Matcher tagContentMatcher = tagContentPattern.matcher(content);

		if (!tagContentMatcher.find()) {
			throw new MTSyntaxException(tagContentRegex, tag);
		}
		if (tagContentMatcher.start() > 0)
			throw new MTSyntaxException(tagContentRegex, tag);

		String fieldValue;
		for (String fieldName : getTagFieldNames(tag)) {
			// Retrieve groupname
			String regexGroupName = fieldName.length() <= 32 ? fieldName : fieldName.substring(0, 32);
			fieldValue = tagContentMatcher.group(regexGroupName);

			if (fieldName.equals("Qualifier")) {
				// Qualifier stored in its own variable
				qualifier = fieldValue;
			} else {
				// null variables stored as empty strings
				if (fieldValue == null)
					fieldValue = "";

				// Additional validation is needed for numeric fields
				if (Tag.isNumericField(fieldName)) {
					validateNumericField(fieldValue);
					// Translate MT number format with commas to numeric format with dots
					fieldValue = fieldValue.replace(',', '.');
				}

				// Add the subfield as a MTEndNode
				subfields.put(fieldName, new MTEndNode(fieldName, null, fieldValue));
			}

		}

		// Give a warning if there are characters after the match which cannot be parsed
		// as part of the tag
		int noOverrunChars = content.length() - tagContentMatcher.end();
		if (noOverrunChars > 0) {
			System.out.println(String.format(
					"[Warning] FieldOverrunError: An additional %d characters could not be successfully be treated as part of any subfield in tag %s.",
					noOverrunChars, tag));
		}

		return new Tag(tag, content, qualifier, subfields);

	}

	/**
	 * Performs additional validation on the contents of a numeric field not already
	 * programmed in the regex-components.
	 * 
	 * @param value The matched numeric field content
	 * @throws MTSyntaxException If the {@code value}-parameter is not a propperly
	 *                           formatted numeric format in accordance with the MT
	 *                           standard.
	 */
	private void validateNumericField(String value) throws MTSyntaxException {
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
			throw new MTSyntaxException(error);
		}
	}

	/**
	 * Get the names of the different fields in a given tag
	 */
	private ArrayList<String> getTagFieldNames(String tag) throws UnknownTagException {
		if (tagFields.containsKey(tag)) {
			return tagFields.get(tag);
		}
		throw new UnknownTagException(tag);
	}

	/**
	 * Get the regex to be used in matching the contents of a tag
	 * 
	 * @throws UnknownTagException If the tag is not part of the MT standard.
	 */
	String getTagRegex(String tag) throws UnknownTagException {
		if (tagRegex.containsKey(tag)) {
			return tagRegex.get(tag);
		}
		throw new UnknownTagException(tag);
	}

}
