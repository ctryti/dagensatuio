package no.ctryti.dagensatuio;

import java.util.Calendar;


public class DinnerItem {
	
	private String  place;
	private String  day;
	private Type    type;
	private Calendar date;
	private String  description;
	private String  period;
	private boolean gluten;
	private boolean laktose;
	
	public enum Type {
		DAGENS, VEGETAR, HALAL, MMM, SUPPE, OPTIMA;
	}

	public DinnerItem(String place, String day, String type, String description, String period, boolean gluten, boolean laktose) {
		this(place, day, Type.valueOf(type), description, period, gluten, laktose);
	}
	
	public DinnerItem(String place, String day, Type type, String description, String period, boolean gluten, boolean laktose) {
		this.place = place;
		this.day = day;
		this.type = type;
		this.description = description;
		this.period = period;
		this.gluten = gluten;
		this.laktose = laktose;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getType() {
		return type.toString();
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isGluten() {
		return gluten;
	}

	public void setGluten(boolean gluten) {
		this.gluten = gluten;
	}

	public boolean isLaktose() {
		return laktose;
	}

	public void setLaktose(boolean laktose) {
		this.laktose = laktose;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getPeriod() {
		return period;
	}
}
