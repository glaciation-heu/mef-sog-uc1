package it.mef.tm.elaboration.timb.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.springframework.stereotype.Service;

import it.mef.tm.elaboration.timb.db.entities.DTracciatoTimbratureH;
import it.mef.tm.elaboration.timb.exception.NotRollBackException;
import it.mef.tm.elaboration.timb.model.DTimbraturaFisica;
import it.mef.tm.elaboration.timb.model.DTimbraturaFisicaRaw;
import it.mef.tm.elaboration.timb.model.TimbraturaFisicaRawScarto;

/**
 * TimbratureService.java
 * interfaccia del servizio per le timbrature
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  23 mag 2024
 * @Description prima versione
 */
@Service
public interface TimbratureService {

	/**
	 * Verifico la data e l'ora
	 * @param timbrNormalizzata
	 * @param raw
	 * @param tracciato
	 * @throws NotRollBackException
	 */
	void verifyDate(DTimbraturaFisicaRaw raw, DTracciatoTimbratureH tracciato) throws NotRollBackException;
	
	/**
	 * metodo per verificare una singola timbratura xml
	 * 
	 * @param rawInput       timbrature grezze
	 * @param timbrAcquisite timbrature acquisite
	 * @param rawScartate    timbrature grezze scartate
	 * @param entity         file
	 * @param tracciato
	 * @param configurazione
	 */
	void normalizzaTimbrature(List<DTimbraturaFisicaRaw> rawInput, List<DTimbraturaFisica> timbrAcquisite,
			List<TimbraturaFisicaRawScarto> rawScartate, DTracciatoTimbratureH tracciato, Path filePath) throws IOException;
}
