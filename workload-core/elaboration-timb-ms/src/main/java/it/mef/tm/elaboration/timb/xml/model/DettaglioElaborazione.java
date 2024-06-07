package it.mef.tm.elaboration.timb.xml.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FileTimbrature.java
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  17 mag 2024
 * @Description prima versione
 */
@Data
@XmlRootElement(name="dettaglio-elaborazione")
@XmlAccessorType(XmlAccessType.NONE)
// Occorre per JAXB il costruttore senza parametri
@NoArgsConstructor
public class DettaglioElaborazione {
	@XmlAttribute(name = "file-name")
	private String fileName;

	@XmlAttribute(name="esito")
	private String esito;

	@XmlAttribute(name="total")
	private int totalNumber;

	@XmlAttribute(name="acquired")
	private int acquired;
	
	@XmlElementWrapper(name = "timbrature")
	@XmlElement(name = "timbratura")
	private List<RowTimbratura> timbrature;
	
	@XmlElementWrapper(name = "timbrature-scartate")
	@XmlElement(name = "timbratura")
	private List<ErroreEsitoTracciato> timbraturaScarto;
	
	public DettaglioElaborazione (String nameFile) {
		this.fileName = nameFile;
	}
}