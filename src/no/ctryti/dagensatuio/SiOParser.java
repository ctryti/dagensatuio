package no.ctryti.dagensatuio;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Scanner;

import android.content.Context;

//import android.util.Log;

public abstract class SiOParser extends Context {
	
	private static final String TAG = "SiOParser";
	
	private static final String NEW_LINE_TOKEN = "&";
	private static final String END_OF_MENU = "&&";
 
	private static final String days[] = {
		"Mandag",
		"Tirsdag",
		"Onsdag",
		"Torsdag",
		"Fredag",
		"&&"
	};
	
	
	
	
	private static HashMap<String, Integer> calConstants;
	static {
		calConstants = new HashMap<String, Integer>();
		calConstants.put("januar", Calendar.JANUARY);
		calConstants.put("februar", Calendar.FEBRUARY);
		calConstants.put("mars", Calendar.MARCH);
		calConstants.put("april", Calendar.APRIL);
		calConstants.put("mai", Calendar.MAY);
		calConstants.put("juni", Calendar.JUNE);
		calConstants.put("juli", Calendar.JULY);
		calConstants.put("august", Calendar.AUGUST);
		calConstants.put("september", Calendar.SEPTEMBER);
		calConstants.put("oktober", Calendar.OCTOBER);
		calConstants.put("november", Calendar.NOVEMBER);
		calConstants.put("desember", Calendar.DECEMBER);
	}
	
	private static String curToken;
	private static Scanner sc;
	private static String place;
	private static String period;
	private static Calendar date;

	
	/* used for testing! */
//	public static void main(String[] args) {
//		ArrayList<DinnerItem> items;
//		try {
//			items = parse(new FileInputStream(new File("./kafe+helga")), "Kafe Helga");
//			for(DinnerItem item : items) {
//				System.out.print(item.getPeriod());
//				System.out.print(" - "+item.getDay());
//				System.out.print(" - "+item.getType());
//				System.out.print(" - "+item.getDescription());
//				System.out.println();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.exit(1);
//		}
//	}
	
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
		String lastDay = "";
		String month = "";

		date = new GregorianCalendar();
				
		while (sc.hasNext()) {
			curToken = sc.next();
			if (curToken.equals("Uke")) {
				sc.next(); // skip week number
				
				//curToken = sc.next(); // should be NEW_LINE_TOKEN
				curToken = sc.next(); // skip all NEW_LINE_TOKENS
				while(curToken.equals(NEW_LINE_TOKEN))
					curToken = sc.next();
				
				sc.next(); // skip '-'
				lastDay = sc.next(); // get the end of the period
				month = sc.next(); // get the month
				while (sc.hasNext()) {
					curToken = sc.next();
					if (!curToken.equals("Mandag")) {
						period += curToken + " ";
					} else {
						break;
					}
				}
				break;
			}
		}
		
		lastDay = lastDay.replace(".", "");
		date.setMinimalDaysInFirstWeek(3); // European way of counting weeks.
		date.set(date.get(Calendar.YEAR), date.get(calConstants.get(month.toLowerCase())), Integer.parseInt(lastDay));
		period = date.get(Calendar.YEAR)+""+date.get(Calendar.WEEK_OF_YEAR);
		
		/* The current token should now be "Mandag" */
		for (int i = Calendar.MONDAY; i <= Calendar.FRIDAY; i++) {
			/* Frederikke Kafe is a special case, with extra shit html */
			if (place.equals("Frederikke kaf\u00e9")) {
				menuEntries.addAll(Arrays.asList(parseFrederikke(i)));
			} else if(place.equals("SV Kafeen")) {
				menuEntries.addAll(Arrays.asList(parseSV(i)));
			} else {
				menuEntries.add(parseNormal(i));
			}
		}
		checkGlutenAndLaktose(menuEntries);
		return menuEntries;
	}

	private static DinnerItem[] parseSV(int day) {
		DinnerItem[] items = new DinnerItem[3];

		for (int i = 0; i < items.length; i++)
			items[i] = new DinnerItem(place, day, (DinnerItem.Type)null, null, period, false, false);

		items[0].setType(DinnerItem.Type.DAGENS);
		items[1].setType(DinnerItem.Type.VEGETAR);
		items[2].setType(DinnerItem.Type.OPTIMA);
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
				
		sc.useDelimiter(" ");
		while (!curToken.equals("Optima:"))
			curToken = sc.next();

		/* skip two NEW_LINE_TOKEN's */
		sc.next();
		sc.next();
		sc.useDelimiter(NEW_LINE_TOKEN);
	
		items[2].setDescription(cleanUpString(sc.next()));
		sc.useDelimiter(" ");

		return items;
	}
	
	private static DinnerItem[] parseFrederikke(int day) {
		DinnerItem[] items = new DinnerItem[6];

		for (int i = 0; i < items.length; i++)
			items[i] = new DinnerItem(place, day, (DinnerItem.Type)null, null, period, false, false);

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

		/* 1 or more NEW_LINE_TOKENS separate these descriptions */
		String str;
		str = sc.next();
		while(str.length() <= 1)
			str = sc.next();
		items[1].setDescription(cleanUpString(str));
		str = sc.next();
		while(str.length() <= 1)
			str = sc.next();
		items[2].setDescription(cleanUpString(str));
		str = sc.next();
		while(str.length() <= 1)
			str = sc.next();
		items[3].setDescription(cleanUpString(str));
		
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
	
	/* find out which dishes are without laktose and/or gluten  */
	private static void checkGlutenAndLaktose(ArrayList<DinnerItem> items) {
		
		for(DinnerItem item : items) {
			String desc = item.getDescription();
			if(desc.contains(" *")) {
				item.setGluten(true);
				desc = desc.replace(" *", "");
			}
			if(desc.contains(" L")) {
				item.setLaktose(true);
				desc = desc.replace(" L", "");
			}
			item.setDescription(desc);
		}
	}
	
	/* used by cafees with only 1 menu-item */
	private static DinnerItem parseNormal(int day) {
		String description = "";
		boolean gluten = false, laktose = false;
		while (sc.hasNext()) {
			curToken = sc.next();
			if (!curToken.equals(days[day - 1])) {
				description += curToken + " ";
			} else {
				break;
			}
		}
		description = cleanUpString(description.replaceAll(NEW_LINE_TOKEN + "\\s*", ""));
		return new DinnerItem(place, day, DinnerItem.Type.DAGENS, description, period, gluten, laktose);
	}

	/*  */
	private static String cleanUpString(String s) {
		s = s.replaceAll(" - ", "");
		s = s.trim();
		return s;
	}
}
