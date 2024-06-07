package it.mef.tm.elaboration.timb.util;

/**
 * StatoAcquisizioneTimbratureEnum.java
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  28 mag 2024
 * @Description prima versione
 */
public enum StatoAcquisizioneTimbratureEnum {

	ACQ_FILE_OK("OK_FILE_ACQUISITION"),
	ACQ_TIMBRATURE_RAW_OK("OK_RAW_ACQUISITION"),
	ACQ_TIMBRATURE_RAW_ERR("KO_RAW_ACQUISITION"),
	ACQ_TIMBRATURE_OK("OK"),
	ACQ_TIMBRATURE_ERR("KO_ACQUISITION");
		
	private String labelStato;
	 
	/**
	 * costruttore della classe
	 */
	private StatoAcquisizioneTimbratureEnum(String labelStato) {
		this.labelStato = labelStato;
	}
	
	/**
	 * metodo per ottenere la label dei log
	 */
	public String getLabelStato() {
		return this.labelStato;
	}
	
}
