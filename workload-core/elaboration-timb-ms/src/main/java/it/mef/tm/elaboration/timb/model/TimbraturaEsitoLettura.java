package it.mef.tm.elaboration.timb.model;

import java.io.Serializable;
import java.util.List;

import it.mef.tm.elaboration.timb.util.StatoAcquisizioneTimbratureEnum;
import lombok.Data;

/**
 * TimbraturaEsitoLettura.java
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  28 mag 2024
 * @Description prima versione
 */
@Data
public class TimbraturaEsitoLettura implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3256569210935363174L;
	private List<TimbraturaFisicaRawScarto> rawScartate;
	private List<DTimbraturaFisica> timbrAcquisite;
	private StatoAcquisizioneTimbratureEnum statoAcquisizioneFinale;
	private String error;
}
