package it.mef.tm.elaboration.timb.xml.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import it.mef.tm.elaboration.timb.model.DTimbraturaFisicaRaw;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TimbraturaFisicaRaw.java
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  17 mag 2024
 * @Description prima versione
 */
@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
//Occorre per JAXB il costruttore senza parametri
@NoArgsConstructor
public class TimbraturaFisicaRaw {
	@XmlElement
	private String badge;
	@XmlElement
	private String causale;
	@XmlElement
	private String concentratore;
	@XmlElement
	private String lettore;
	@XmlElement
	private String testina;
	@XmlElement
	private String timbratura;
	@XmlElement
	private String data;
	@XmlElement
	private String ora;
	@XmlElement
	private String verso;

	public TimbraturaFisicaRaw (DTimbraturaFisicaRaw tfr) {
		this.badge = tfr.getDescCodiceBadge();
		this.causale = tfr.getDescCodiceCausale();
		this.concentratore = tfr.getDescCodiceConcentratore();
		this.lettore = tfr.getDescCodiceLettore();
		this.testina = tfr.getDescCodiceTestina();
		this.timbratura = tfr.getDescCodiceTimbratura();
		this.data = tfr.getDescData();
		this.ora = tfr.getDescOra();
		this.verso = tfr.getDescVerso();
	}
}