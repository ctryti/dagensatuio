package no.ctryti.dagensatuio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class SiOParser {
	private static final String NEW_LINE_TOKEN = "§";
	private static final String END_OF_MENU = "&&";
 
	
	
	private static final String days[] = {
		"Mandag",
		"Tirsdag",
		"Onsdag",
		"Torsdag",
		"Fredag",
		"&&"
	};

	private static String curToken;
	private static Scanner sc;
	private static String place;
	private static String period;

	public static void main(String[] args) {
		InputStream file;
		ArrayList<DinnerItem> items;
		try {
//			file = new FileInputStream(new File("./frederikke+kafe"));
//			items = SiOParser.parse(file, "Frederikke kafé");
//			for (DinnerItem dinnerItem : items) {
//				System.out.print(dinnerItem.getPlace() + ": ");
//				System.out.print(dinnerItem.getDay() + " - ");
//				System.out.print(dinnerItem.getType() + " - ");
//				System.out.println(dinnerItem.getDescription());
//				
//			}
//			file = new FileInputStream(new File("./informatikkafeen+ny"));
//			items = SiOParser.parse(file, "Informatikkafeen");
//			for (DinnerItem dinnerItem : items) {
//				System.out.print(dinnerItem.getPlace() + ": ");
//				System.out.print(dinnerItem.getDay() + " - ");
//				System.out.print(dinnerItem.getType() + " - ");
//				System.out.println(dinnerItem.getDescription() + " - ");
//				
//			}
			file = new FileInputStream(new File("./sv+kafeen+ny"));
			items = SiOParser.parse(file, "SV Kafeen");
			for (DinnerItem dinnerItem : items) {
				System.out.print(dinnerItem.getPlace() + ": ");
				System.out.print(dinnerItem.getDay() + " - ");
				System.out.print(dinnerItem.getType() + " - ");
				System.out.println(dinnerItem.getDescription() + " - ");
				
			}
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		
	}
		
	public static String replaceAll(String source, String toReplace, String replacement) {
		int idx = source.lastIndexOf( toReplace );
		if ( idx != -1 ) {
			StringBuffer ret = new StringBuffer( source );
			ret.replace( idx, idx+toReplace.length(), replacement );
			while( (idx=source.lastIndexOf(toReplace, idx-1)) != -1 ) {
				ret.replace( idx, idx+toReplace.length(), replacement );
			}
			source = ret.toString();
		}

		return source;
	}

	
	public static ArrayList<DinnerItem> parse(InputStream source, String place) {

		ArrayList<DinnerItem> menuEntries = new ArrayList<DinnerItem>();

		String content;
		String treatedContent;

		SiOParser.place = place;
		curToken = "";
		treatedContent = "";
		content = "";
		period = "";

		sc = new Scanner(source);
		while (sc.hasNextLine()) {
			content += sc.nextLine() + NEW_LINE_TOKEN;
		}
		/* Remove all html tags */
		treatedContent = content.replaceAll("\\<BR\\>"," "+NEW_LINE_TOKEN+" ");
		treatedContent = treatedContent.replaceAll("\\<br\\>"," "+NEW_LINE_TOKEN+" ");
		treatedContent = treatedContent.replaceAll("\\<.*?\\>", " ");
		
		/* replace the nbsp entities */
		treatedContent = treatedContent.replaceAll("&nbsp;", " ");
		/* compact multiple whitespaces into 1 space */
		treatedContent = treatedContent.replaceAll("\\s+", " ");
		
		/*
		 * the string "* = Uten" marks the end of the menu, replace it with an
		 * easier to spot indicator
		 */
		treatedContent = treatedContent.replace("* = Uten", END_OF_MENU);

		sc = new Scanner(treatedContent);

		/*
		 * Scan to the token "Uke" and save the following string until "Mandag"
		 * as the period
		 */
		while (sc.hasNext()) {
			curToken = sc.next();
			if (curToken.equals("Uke")) {
				period = curToken + " ";
				while (sc.hasNext()) {
					curToken = sc.next();
					if (!curToken.equals(days[0])) {
						period += curToken + " ";
					} else {
						break;
					}
				}
				break;
			}
		}

		/* remove any NEW_LINE_TOKENS that got into the period string */
		period = period.replaceAll(NEW_LINE_TOKEN, "");
		
		/* The current token should now be "Mandag" */
		for (int i = 0; i < 5; i++) {
			/* Frederikke Kafe is a special case, with extra shit html */
			if (place.equals("Frederikke kafé")) {
				menuEntries.addAll(Arrays.asList(parseShittyFrederikkeHtml(i)));
			} else if(place.equals("SV Kafeen")) {
				menuEntries.addAll(Arrays.asList(parseShittySVHtml(i)));
			} else {
				menuEntries.add(parseSingleDay(i));
			}
		}
		return menuEntries;
	}

	private static DinnerItem[] parseShittySVHtml(int num) {
		DinnerItem[] items = new DinnerItem[3];

		/***************************************/
		
//		while(sc.hasNext()) {
//			System.out.println(sc.nextLine());
//		}
//		if(!sc.hasNext())
//			return null;
		
		/****************************************/
		
		for (int i = 0; i < items.length; i++)
			items[i] = new DinnerItem(place, days[num], null, null, false, false);

		items[0].setType(DinnerItem.Type.DAGENS);
		items[1].setType(DinnerItem.Type.VEGETAR);
		items[2].setType(DinnerItem.Type.OPTIMA);

//		while(sc.hasNext()) {
//			System.out.println(sc.nextLine());
//		}
//		if(!sc.hasNext())
//			return null;

		sc.useDelimiter(" ");
		/* First find and create DAGENS */
		curToken = sc.next();
		while (!curToken.equals("Dagens:")) {
			curToken = sc.next();
		}
		sc.next();
		sc.next();
		sc.useDelimiter(NEW_LINE_TOKEN);
		items[0].setDescription(cleanUpString(sc.next()));

		System.out.println(items[0].getDescription());
		
		sc.useDelimiter(" ");
		while (!curToken.equals("Vegetar:") && !curToken.equals("Halal:"))
			curToken = sc.next();

		if(curToken.equals("Halal:"))
			items[1].setType(DinnerItem.Type.HALAL);
		
		/* skip two NEW_LINE_TOKEN's */
		sc.next();
		sc.next();
		sc.useDelimiter(NEW_LINE_TOKEN);
		items[1].setDescription(cleanUpString(sc.next()));
		System.out.println(items[1].getDescription());
		
		sc.useDelimiter(" ");
		while (!curToken.equals("Optima:"))
			curToken = sc.next();

		/* skip two NEW_LINE_TOKEN's */
		sc.next();
		sc.next();
		sc.useDelimiter(NEW_LINE_TOKEN);
	
		items[2].setDescription(cleanUpString(sc.next()));
		System.out.println(items[2].getDescription());
		sc.useDelimiter(" ");

		return items;
	}
	
	private static DinnerItem[] parseShittyFrederikkeHtml(int num) {
		DinnerItem[] items = new DinnerItem[6];


		
		for (int i = 0; i < items.length; i++)
			items[i] = new DinnerItem(place, days[num], null, null, false, false);

		items[0].setType(DinnerItem.Type.DAGENS);
		items[1].setType(DinnerItem.Type.VEGETAR);
		items[2].setType(DinnerItem.Type.HALAL);
		items[3].setType(DinnerItem.Type.MMM);
		items[4].setType(DinnerItem.Type.SUPPE);
		items[5].setType(DinnerItem.Type.SUPPE);

		/* First find and create DAGENS */
		SiOParser.curToken = sc.next();
		while (!curToken.equals("Dagens"))
			curToken = sc.next();

		sc.next();
		sc.next();
		sc.useDelimiter(NEW_LINE_TOKEN);
		items[0].setDescription(cleanUpString(sc.next()));
		sc.useDelimiter(" ");

		while (!curToken.equals("Mmm+"))
			curToken = sc.next();

		/* skip two NEW_LINE_TOKEN's */
		sc.next();
		sc.next();

		sc.useDelimiter(NEW_LINE_TOKEN);
		items[1].setDescription(cleanUpString(sc.next()));
		items[2].setDescription(cleanUpString(sc.next()));
		items[3].setDescription(cleanUpString(sc.next()));

		
		sc.useDelimiter(" ");

		while (!sc.next().equals("Suppe"))
			;
		sc.next();
		sc.next();
		sc.next();
		sc.useDelimiter(" - ");
		items[4].setDescription(cleanUpString(sc.next().replaceAll(NEW_LINE_TOKEN, "")));
		sc.useDelimiter(NEW_LINE_TOKEN);
		items[5].setDescription(cleanUpString(sc.next().replaceAll(NEW_LINE_TOKEN, "")));
		sc.useDelimiter(" ");
		return items;
	}

	private static DinnerItem parseSingleDay(int num) {
		String description = "";
		boolean gluten = false, laktose = false;
		while (sc.hasNext()) {
			curToken = sc.next();
			if (!curToken.equals(days[num + 1])) {
				description += curToken + " ";
			} else {
				break;
			}
		}
		description = cleanUpString(description.replaceAll(NEW_LINE_TOKEN + "\\s*", ""));
		return new DinnerItem(place, days[num], DinnerItem.Type.DAGENS, description, gluten, laktose);
	}

	private static String cleanUpString(String s) {
		s = s.replaceAll(" - ", "");
		s = s.trim();
		return s;
	}
}
