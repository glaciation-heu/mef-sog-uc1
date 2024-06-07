package it.mef.tm.elaboration.timb.util;

import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * StringUtility.java
 * classe di util per le stringhe
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  4 apr 2024
 * @Description prima versione
 */
@NoArgsConstructor(access = AccessLevel.NONE)
public class DateUtility {
	/** FORMATO_ITALY dd/MM/yyyy */
	public static final String FORMATO_ITALY = "dd/MM/yyyy";
	/** FORMATO_DB yyyy-MM-dd */
	public static final String FORMATO_DB = "yyyy-MM-dd";
	/** FORMATO_TIME_DB HH:mm:ss */
	public static final String FORMATO_TIME_DB = "HH:mm:ss";
	public static final String MEZZANOTTE = "00:00:00";
    
	
	/**
	 * Compare fra due date 
	 * Se l'argomento dataOra è false con precisione al "giorno" (non viene considerato l'orario)
	 * Se l'argomento dataOra è true la precisione è al "minuto" (non vengono considerati i secondi)
	 * @param d1
	 * @param d2
	 * @param dataOra
	 * @return restituisce 0 se le date sono uguali; 
	 * restituisce un valore minore a 0 se d1 è precedente a d2; 
	 * restituisce un valore maggiore a 0 se d1 è successivo a d2.
	 */
	public static int compare(Date d1, Date d2, boolean dataOra) {
		d1 = impostaOrarioData(d1, dataOra);
		d2 = impostaOrarioData(d2, dataOra);
		return d1.compareTo(d2);
	}
	
	/**
	 * Compare fra due date con precisione al "giorno" 
	 * non viene considerato l'orario
	 * @param d1
	 * @param d2
	 * @return restituisce 0 se le date sono uguali; 
	 * restituisce un valore minore a 0 se d1 è precedente a d2; 
	 * restituisce un valore maggiore a 0 se d1 è successivo a d2.
	 */
	public static int compare(Date d1, Date d2) {
		return compare(d1,d2, false);
	}

	/**
	 * metodo che modifica la data di input modificando l'orario nelle 00:00:00
	 * @param dataCalcolo
	 * @return
	 */
	public static java.util.Date impostaOrarioData(java.util.Date dataCalcolo, boolean dataOra){
		GregorianCalendar gc = new GregorianCalendar();
    	TimeZone tz = TimeZone.getTimeZone("Europe/Rome");
    	gc.setTimeZone(tz);
    	try {
    		gc.setTime(dataCalcolo);
    		
    		if (!dataOra) { 
    			// se non è richiesta la precisione all'orario azzero ore e minuti
	    		gc.set(Calendar.AM_PM, 0);
	        	gc.set(Calendar.HOUR_OF_DAY, 0);
	        	gc.set(Calendar.MINUTE, 0);
    		}
    		
        	gc.set(Calendar.SECOND, 0);
        	gc.set(Calendar.MILLISECOND, 0);
        } catch (Exception e) {	
			return dataCalcolo;
		}
    	return gc.getTime();
	}


	/**
	 * metodo che converte un oggetto String in un oggetto sql.Date
	 * 
	 * @param dataConvertire
	 * @return date
	 */
	public static Date castStringToDate(String dataConvertire,
			String formato) throws ParseException {
		return castStringToDateExecute(dataConvertire, formato);
	}

	private static Date castStringToDateExecute(String dataConvertire,
			String formato) throws ParseException {
		DateFormat format = new SimpleDateFormat(formato);
		format.setLenient(false);
		java.util.Date date = format.parse(dataConvertire);
		Date sqlStartDate = new java.sql.Date(date.getTime());
		return sqlStartDate;
	}
	
	/**
	 * restituisce la data che, per convezione, determina l'infinito
	 * 
	 * @return
	 */
	public static Date getEndPeriodDate() {
		String str = "9999-12-31";
		try {
			return castStringToDate(str, FORMATO_DB);
		} catch (ParseException e) {
		}
		return null;
	}
	
	/**
	 * Metodo che, a partire dal numero di secondi dalla mezzanotte,
	 * restituisce l'ora in formato hh:mm
	 * @param time
	 * @return
	 */
	public static String getHoursMinutesFromTimeSSS(Long time) {
		return getHoursMinutesFromTimeSSS(time.doubleValue());
	}
	
	/**
	 * Metodo che, a partire dal numero di secondi dalla mezzanotte,
	 * restituisce l'ora in formato hh:mm
	 * @param time
	 * @return
	 */
	public static String getHoursMinutesFromTimeSSS(double time) {
		NumberFormat nf = DecimalFormat.getInstance();
		nf.setMaximumFractionDigits(0);
		nf.setRoundingMode(RoundingMode.DOWN);
		String ore = nf.format((time / 3600));
		String minuti = nf.format((time % 3600)/60);
		if(ore.length() == 1) {
			ore = "0" + ore;
		}
		if(minuti.length() == 1) {
			minuti = "0" + minuti;
		}
		return ore + ":" + minuti;
	}

	/**
	 * Metodo che, a partire dal numero di secondi dalla mezzanotte,
	 * restituisce l'ora in formato hh:mm:ss
	 * @param time
	 * @return
	 */
	public static String getHoursFromTimeSSS(String time) {
		int totalSecs = Integer.parseInt(time);
		int hours = totalSecs / 3600;
		int minutes = (totalSecs % 3600) / 60;
		int seconds = totalSecs % 60;
		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}
	
	/**
	 * Metodo che, a partire dal numero di minuti dalla mezzanotte,
	 * restituisce l'ora in formato hh:mm:ss
	 * @param time
	 * @return
	 */
	public static String getHoursFromTimeMMM(String time) {
		int totalMins = Integer.parseInt(time);
		int hours = totalMins / 60;
		int minutes = totalMins % 60;
		return String.format("%02d:%02d:00", hours, minutes);
	}
}
