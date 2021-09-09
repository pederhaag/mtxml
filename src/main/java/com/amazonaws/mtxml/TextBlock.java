package com.amazonaws.mtxml;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextBlock implements MTComponent {
	private final static String regexPatternBlock = "\\{4:\\r?\\n(?<TagContent>(?>.|\\r|\\n)*)^-\\}$";
	private final static String regexPatternTags = ":([^:]+):([^:]*(?>\\d|\\w))";
	private final String RawData;

	TextBlock(String content) {
		if (content == null) {
			throw new NullPointerException("Argument cannot be null.");
		}
		Pattern pattern = Pattern.compile(regexPatternBlock, Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(content);

		if (!matcher.find()) {
			throw new SyntaxException(regexPatternBlock, content);
		}
		RawData = matcher.group("TagContent");

		Pattern patternTags = Pattern.compile(regexPatternTags, Pattern.MULTILINE);
		Matcher matcherTags = patternTags.matcher(RawData);

		while (matcherTags.find()) {
			String tagString = matcherTags.group(0);
			String tag = matcherTags.group(1);
			String value = matcherTags.group(2);

			// TODO: Her er jeg. Paa tide aa lage tags/blocks
		}

	}

	public static void main(String[] args) {
		String textBlock = null;
		try {
			String newLine = System.getProperty("line.separator");
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
	}

	@Override
	public String toXml() {
		// TODO Auto-generated method stub
		return null;
	}

}
