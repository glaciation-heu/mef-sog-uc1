package it.mef.tm.scheduled.client.util.model;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ElaborazioneModel.java
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  9 apr 2024
 * @Description prima versione
 */
@Data
@NoArgsConstructor
public class ElaborazioneModel implements Serializable {

	/**
	 * Campo serialVersionUID
	 */
	private static final long serialVersionUID = 4412814994280538856L;
	
	/**
	 * path al file
	 */
	private String pathToFile;
	
}
