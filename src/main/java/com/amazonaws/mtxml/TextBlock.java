package com.amazonaws.mtxml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextBlock implements MTComponent {
	private final static String REGEX_PATTERN_TEXTBLOCK = "\\{4:\\r?\\n(?<TagContent>(?>.|\\r|\\n)*)\\n-\\}$";
	private final static String REGEX_PATTERN_TAGS = ":(?<Tag>[\\dA-Z]+):(?<Content>(?>:)?(?>.|\\n)*?(?=\\n:|\\n-\\}))";
	private final String RawData;
	private final TagFactory factory;
	private ArrayList<MTComponent> components = new ArrayList<MTComponent>();

	TextBlock(String content) throws IOException, UnknownTagException, MTException {
		factory = new TagFactory();

		if (content == null) {
			throw new NullPointerException("Argument cannot be null.");
		}
		Pattern pattern = Pattern.compile(REGEX_PATTERN_TEXTBLOCK, Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(content);

		if (!matcher.find()) {
			throw new SyntaxException(REGEX_PATTERN_TEXTBLOCK, content);
		}
		RawData = matcher.group("TagContent");

		Pattern patternTags = Pattern.compile(REGEX_PATTERN_TAGS, Pattern.MULTILINE);
		Matcher matcherTags = patternTags.matcher(content);
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
		String opening = XmlFactory.openNode("TextBlock");

		String content = "";
		for (MTComponent c : components) {
			content += c.toXml();
		}

		String closing = XmlFactory.closeNode("TextBlock");

		return opening + content + closing;
	}

	public static void main(String[] args) throws Exception {
		String textBlock = null;
		try {
//			String newLine = System.getProperty("line.separator");
			String newLine = "\n";
			File myObj = new File(
					"C:\\Users\\peder\\programming\\hobby\\mtxml\\src\\test\\resources\\textblockinput1.txt");
			Scanner myReader = new Scanner(myObj);
			StringBuilder sb = new StringBuilder();
			while (myReader.hasNextLine()) {
				sb.append(myReader.nextLine());
				if (myReader.hasNextLine())
					sb.append(newLine);
			}
			myReader.close();
			textBlock = sb.toString();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		TextBlock textBlockObj = new TextBlock(textBlock);
		System.out.println(textBlockObj.toXml());
	}

}
