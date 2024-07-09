package it.mef.tm.scheduled.client.costants;

import java.util.stream.Stream;

public enum GiorniSettimana {
	MONDAY(1,"LUN","MON"),
	TUESDAY(2,"MAR","TUE"),
	WEDNESDAY(3,"MER","WED"),
	THURSDAY(4,"GIO","THU"),
	FRIDAY(5,"VEN","FRI"),
	SATURDAY(6,"SAB","SAT"),
	SUNDAY(7,"SOM","SUN");

	private int numDay;
	private String[] acceptedPattern;
	/**
	 * 
	 */
	private GiorniSettimana(int i, String... acceptedPattern) {
		this.numDay = i;
		this.acceptedPattern = acceptedPattern;
	}
	
	public static final boolean verifyDay(String day, int dayOfWeek) {
		GiorniSettimana giorno = Stream.of(GiorniSettimana.values()).filter(g -> g.numDay == dayOfWeek).findFirst().orElse(null);
		return giorno != null && (Stream.of(giorno.acceptedPattern).anyMatch(day::contains) || day.contains(String.valueOf(giorno.numDay)));
	}
}
