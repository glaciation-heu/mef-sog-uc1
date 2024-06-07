package it.mef.tm.elaboration.timb.xml.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Timbratura.java
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  17 mag 2024
 * @Description prima versione
 */
@Data
//Occorre per JAXB il costruttore senza parametri
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "timbratura")
@XmlAccessorType(XmlAccessType.NONE)
public class RowTimbratura {
	@XmlElement(name = "timbratura-fisica")
	private TimbraturaFisica timbratura;
	@XmlElement(name = "timbratura-fisica-grezza")
	private TimbraturaFisicaRaw timbraturaRaw;
}