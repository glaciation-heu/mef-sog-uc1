package it.mef.tm.elaboration.timb.xml.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import it.mef.tm.elaboration.timb.model.TimbraturaFisicaRawScarto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * TimbraturaFisicaRawScarto.java
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  29 mag 2024
 * @Description prima versione
 */
@Data
@EqualsAndHashCode(callSuper = true)
//Occorre per JAXB il costruttore senza parametri
@NoArgsConstructor
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class ErroreEsitoTracciato extends TimbraturaFisicaRaw {
	@XmlElement(name="identificativo-timbratura")
	private String idTimbratura;

	@XmlElement(name="codice")
	private int codice = 1;
	
	@XmlElement(name="descrizione")
	private String descrizione;
	
	public ErroreEsitoTracciato (TimbraturaFisicaRawScarto scarto) {
		super(scarto);
		this.codice = 1; // ANNULLAMENTO_GENERIC = 1
		this.idTimbratura = scarto.getDescCodiceTimbratura();
		this.descrizione = scarto.getEccezioneScarto().getCode();
	}
}