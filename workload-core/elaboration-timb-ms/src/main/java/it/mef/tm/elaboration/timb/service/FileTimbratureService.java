package it.mef.tm.elaboration.timb.service;

import java.nio.file.Path;

import it.mef.tm.elaboration.timb.exception.PreconditionException;
import it.mef.tm.elaboration.timb.model.TimbraturaEsitoLettura;

/**
 * FileTimbratureService.java
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  17 mag 2024
 * @Description prima versione
 */
public interface FileTimbratureService  {

	/**
	 * Metodo avviaCaricamentoManuale
	 * @param filePath
	 * @return 
	 * @throws PreconditionException 
	 */
	TimbraturaEsitoLettura avviaCaricamentoManuale(Path filePath) throws PreconditionException;
}
