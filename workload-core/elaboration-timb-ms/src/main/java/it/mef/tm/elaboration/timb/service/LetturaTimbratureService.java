package it.mef.tm.elaboration.timb.service;

import java.io.IOException;

import javax.xml.bind.JAXBException;

/**
 * LetturaTimbratureService.java
 * componente di business della lettura dei file di fornitura timbrature
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  28 mag 2024
 * @Description prima versione
 */
public interface LetturaTimbratureService {

	/**
	 * Servizio di lettura file	
	 * @param pathToFile
	 * @throws JAXBException 
	 * @throws IOException 
	 */
	public void letturaFornitura(String pathToFile) throws JAXBException, IOException ;
}
