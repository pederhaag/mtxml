package com.amazonaws.mtxml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amazonaws.mtxml.utils.XmlFactory;

/**
 * 
 * This class models the Textblock of a MT message
 *
 */
public class TextBlock implements MTComponent {
	/**
	 * Regex for matching the textblock content
	 */
	private final static String REGEX_PATTERN_TEXTBLOCK = "\\{4:\\r?\\n(?<TagContent>(?>.|\\r|\\n)*)?\\n?-\\}$";

	/**
	 * Regex for matching the different tags and their contents
	 */
	private final static String REGEX_PATTERN_TAGS = ":(?<Tag>[\\dA-Z]+):(?<Content>(?>:)?(?>.|\\R)*?(?=\\R:|\\R-\\}))";

	/**
	 * Container for the subcompoentns of the textblock, i.e. tags and tagblocks
	 */
	private ArrayList<MTComponent> components = new ArrayList<MTComponent>();

	TextBlock(String content) throws IOException, UnknownTagException {
		this(content, new TagFactory());
	}

	TextBlock(String content, TagFactory factory) throws UnknownTagException {
		// Get the block content
		Objects.requireNonNull(content, "Block content cannot be null");
		Pattern pattern = Pattern.compile(REGEX_PATTERN_TEXTBLOCK, Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(content);

		// Make sure that the syntax is correct
		if (!matcher.find()) {
			throw new MTSyntaxException(REGEX_PATTERN_TEXTBLOCK, content);
		}

		// Loop through the tags/blocks
		Pattern patternTags = Pattern.compile(REGEX_PATTERN_TAGS, Pattern.MULTILINE);
		Matcher matcherTags = patternTags.matcher(content);
		// This container is used see at which dept the loop is currently working on
		ArrayList<TagBlock> openBlocks = new ArrayList<TagBlock>();
		while (matcherTags.find()) {
			String tagName = matcherTags.group("Tag");
			String tagContent = matcherTags.group("Content");

			if (tagName.equals("16R")) {
				// Start of a new block
				TagBlock newBlock = new TagBlock(tagContent);
				openBlocks.add(newBlock);
				if (openBlocks.size() == 1) {
					// Add to textBlock
					components.add(newBlock);
				} else {
					// Add to parent block
					openBlocks.get(openBlocks.size() - 2).addComponent(newBlock);
				}

			} else if (tagName.equals("16S")) {
				// Remove last block in list
				openBlocks.remove(openBlocks.size() - 1);

			} else if (!tagName.startsWith("16")) {
				// Ordinary tag
				if (openBlocks.size() == 0) {
					// No open blocks
					components.add(factory.createTag(tagName, tagContent));
				} else {
					// Add tag to open block at lowest level
					openBlocks.get(openBlocks.size() - 1).addComponent(factory.createTag(tagName, tagContent));
				}
			} else {
				throw new UnknownTagException(tagName);
			}
		}

	}

	@Override
	public String toXml() {
		String xmlOpening = XmlFactory.openNode("TextBlock");

		String xmlChildren = "";
		for (MTComponent c : components) {
			xmlChildren += c.toXml();
		}

		String xmlClosing = XmlFactory.closeNode("TextBlock");

		return xmlOpening + xmlChildren + xmlClosing;
	}

	public static void main(String[] args) throws Exception {
		new TextBlock("{4:\r\n" + "-}");

//		String textBlock = null;
//		try {
//			String newLine = "\n";
//			File myObj = new File(
//					"C:\\Users\\peder\\programming\\hobby\\mtxml\\src\\test\\resources\\validtextblocks\\textblock8.txt");
//			Scanner myReader = new Scanner(myObj);
//			StringBuilder sb = new StringBuilder();
//			sb.append("{4:\n");
//			while (myReader.hasNextLine()) {
//				sb.append(myReader.nextLine());
//				if (myReader.hasNextLine())
//					sb.append(newLine);
//			}
//			sb.append("\n-}");
//			myReader.close();
//			textBlock = sb.toString();
//		} catch (FileNotFoundException e) {
//			System.out.println("An error occurred.");
//			e.printStackTrace();
//		}
//		TextBlock textBlockObj = new TextBlock(textBlock);
//		System.out.println(textBlockObj.toXml());
	}

}
