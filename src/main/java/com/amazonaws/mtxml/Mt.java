package com.amazonaws.mtxml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amazonaws.mtxml.utils.XmlFactory;

public class Mt implements MTComponent {
	private final static String REGEX_PATTERN = "^(?<BasicHeader>\\{1:\\w*\\})(?<ApplicationHeader>\\{2:\\w*\\})?(?<UserHeader>\\{3:(?>\\{\\w*:[\\w\\/-]*\\})*\\})?(?<TextBlock>\\{4:\\R(?>.*\\R)*-\\})(?<TrailerBlock>\\{5:(?>\\{\\w*:\\w*\\})*\\})$";

	private LinkedHashMap<String, MTComponent> components = new LinkedHashMap<String, MTComponent>();
	private MTComponent basicHeader;
	private MTComponent appHeader = null;
	private MTComponent userHeader = null;
	private MTComponent textBlock = null;
	private MTComponent trailerBlock = null;

	public Mt(String mtContent) throws IOException, UnknownTagException {

		Pattern pattern = Pattern.compile(REGEX_PATTERN);
		Matcher matcher = pattern.matcher(mtContent);

		if (!matcher.find()) {
			throw new MTSyntaxException(REGEX_PATTERN, mtContent);
		}

		// Basic Header
		basicHeader = new BasicHeaderBlock(matcher.group("BasicHeader"));
		components.put("BasicHeader", basicHeader);

		// Application Header
		String appHeaderContent = matcher.group("ApplicationHeader");
		if (appHeaderContent != null) {
			appHeader = new ApplicationHeaderBlock(appHeaderContent);
		}
		components.put("ApplicationHeader", appHeader);

		// User Header
		String userHeaderContent = matcher.group("UserHeader");
		if (userHeaderContent != null) {
			userHeader = new UserHeaderBlock(userHeaderContent);
		}
		components.put("UserHeader", userHeader);

		// Text block
		String textBlockContent = matcher.group("TextBlock");
		if (textBlockContent != null) {
			textBlock = new TextBlock(textBlockContent);
		}
		components.put("TextBlock", textBlock);

		// Trailer block
		String trailerBlockContent = matcher.group("TrailerBlock");
		if (trailerBlockContent != null) {
			trailerBlock = new TrailerBlock(trailerBlockContent);
		}
		components.put("TrailerBlock", trailerBlock);

	}

	public MTComponent getComponent(String comp) {
		if (!components.containsKey(comp))
			return null;
		else
			return components.get(comp);
	}

	public static void main(String[] args) throws IOException, UnknownTagException {
		String input = "{1:F01FOLTNOKKXXXX0883048452}{2:I527CEDELULLXXXXN}{3:{108:2004360.1}}{4:\r\n" + ":16R:GENL\r\n"
				+ ":28E:1/ONLY\r\n" + ":20C::SEME//2021092400002801\r\n" + ":20C::SCTR//DNB BA DEAL 1\r\n"
				+ ":20C::CLCI//20210924000028\r\n" + ":23G:NEWM\r\n" + ":98A::EXRQ//20210924\r\n"
				+ ":98A::TRAD//20180419\r\n" + ":22H::CINT//PADJ\r\n" + ":22H::COLA//SLOA\r\n" + ":22H::REPR//RECE\r\n"
				+ ":22F::AUTA/CEDE/AUTO\r\n" + ":13B::ELIG//BASKET A\r\n" + ":16R:COLLPRTY\r\n"
				+ ":95P::PTYA//FOLTNOKK\r\n" + ":97A::SAFE//21516\r\n" + ":16S:COLLPRTY\r\n" + ":16R:COLLPRTY\r\n"
				+ ":95P::PTYB//DNBANOKX\r\n" + ":16S:COLLPRTY\r\n" + ":16R:COLLPRTY\r\n" + ":95R::TRAG/CEDE/28322\r\n"
				+ ":16S:COLLPRTY\r\n" + ":16S:GENL\r\n" + ":16R:DEALTRAN\r\n" + ":98B::TERM//OPEN\r\n"
				+ ":19A::TRAA//EUR332833133,\r\n" + ":16S:DEALTRAN\r\n" + "-}{5:{MAC:00000000}{CHK:514356C818A3}}";

		Mt msg = new Mt(input);
		String xml = msg.toXml();
//		System.out.println(xml);
		System.out.println(XmlFactory.prettify(xml, 2, true));

	}

	@Override
	public String toXml() {
		StringBuilder sb = new StringBuilder();
		sb.append(XmlFactory.openNode("SwiftMessage"));
		for (MTComponent comp : components.values()) {
			sb.append(comp.toXml());
		}
		sb.append(XmlFactory.closeNode("SwiftMessage"));
		return sb.toString();
	}

}
