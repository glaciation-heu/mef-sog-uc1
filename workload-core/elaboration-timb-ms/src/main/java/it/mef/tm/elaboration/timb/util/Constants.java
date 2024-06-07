package it.mef.tm.elaboration.timb.util;

import lombok.Data;

/**
 * Constants.java
 * costanti ad uso comune
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  23 mag 2024
 * @Description prima versione
 */
@Data
public class Constants {
    public static final String TRUNC = "TRUNC";
    public static final String MEZZANOTTE = "00:00:00";
    public static final String FORMAT_ORARIO = "HH:mm:ss";
    public static final String DATA_INIT_VALID = "dataInizioValidita";
    public static final String DATA_FINE_VALID = "dataFineValidita";
    public static final String CAMPO_CODICE = "codiCodice";
    public static final String CAMPO_DESCRIZIONE = "descDescrizione";
    public static final String BADGE_PK_VS_H = "dBadgeHs";
    public static final String V_AMMINISTRAZIONI_PK = "vAmministrazioniPk";

	public static final String ERR_GFT_SERVICE_TMGFT02 = "NO_ENTITY_FOUND";
	public static final String ERR_GFT_SERVICE_TMGFT03 = "XML_READING_ERROR";
	public static final String ERR_GFT_SERVICE_TMGFT05 = "TIMESTAMPS NOT FOUND";
	public static final String ERR_GFT_SERVICE_TMGFT09 = "DATETIME_VALIDATION_ERROR";
	public static final String ERR_GFT_SERVICE_TMGFT10 = "VERSO_VALIDATION_ERROR";
	public static final String ERR_GFT_SERVICE_TMGFT11 = "LETTORE_NOT_FOUND";
	public static final String ERR_GFT_SERVICE_TMGFT12 = "TESTINA_NOT_FOUND";
	public static final String ERR_GFT_SERVICE_TMGFT12_B = "VERSOTESTINA_VALIDATION_ERROR";
	public static final String ERR_GFT_SERVICE_TMGFT13 = "CAUSALE_VALIDATION_ERROR";
	public static final String ERR_GFT_SERVICE_TMGFT14 = ERR_GFT_SERVICE_TMGFT02;
	public static final String ERR_GFT_SERVICE_TMGFT15 = "BADGE_NOT_FOUND";
	public static final String ERR_GFT_SERVICE_TMGFT19 = "MISSING_LETTORE_OR_TESTINA";
	public static final String ERR_GFT_SERVICE_TMGFT20 = "MISSING_BADGE";
	public static final String ERR_GFT_SERVICE_TMGFT21 = "MISSING_DATE";
	public static final String ERR_GFT_SERVICE_TMGFT22 = "MISSING_TIME";
	public static final String ERR_GFT_SERVICE_TMGFT23 = "MISSING_TESTINA";
	public static final String ERR_GFT_SERVICE_TMGFT25 = "XML_VALIDATION_ERROR";
	public static final String ERR_GFT_SERVICE_TMGFT35 = "NOT_UNIQUE_TESTINA";
    
	public static final String RESULT_EXTENSION = ".result";
	
	public static final String UTF8_BOM = "\uFEFF";
	/**
     * costruttore vuoto
     */
    private Constants() {}
}
