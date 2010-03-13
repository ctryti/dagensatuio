package no.ctryti.dagensatuio;

public class DinnerItem {
	
	private String  place;
	private String  day;
	private Type    type;
	private String  description;
	private boolean gluten;
	private boolean laktose;
	
	public enum Type {
		DAGENS, VEGETAR, HALAL, MMM, SUPPE, OPTIMA;
	}

	public DinnerItem(String place, String day, Type type, String description, boolean gluten, boolean laktose) {
		this.place = place;
		this.day = day;
		this.type = type;
		this.description = description;
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
}
