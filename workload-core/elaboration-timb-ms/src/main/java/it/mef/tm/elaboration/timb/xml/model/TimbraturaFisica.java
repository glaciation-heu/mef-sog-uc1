package it.mef.tm.elaboration.timb.xml.model;

import java.text.SimpleDateFormat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import it.mef.tm.elaboration.timb.model.DTimbraturaFisica;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TimbraturaFisica.java
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
public class TimbraturaFisica {
	@XmlElement(name = "data-ora")
	private String dataOra;
	@XmlElement
	private String verso;
	@XmlElement(name = "assegnazione-badge")
	private Long assegnazioneBadge;
	@XmlElement
	private Long causale;
	@XmlElement(name = "lettore-badge")
	private Long lettoreBadge;
	@XmlElement(name = "testina-lettore-badge")
	private Long testinaLettoreBadge;
	
	public TimbraturaFisica (DTimbraturaFisica tf, SimpleDateFormat sdf) {
		this.dataOra = sdf.format(tf.getDttmDataOra());
		this.verso = tf.getStatVerso();
		this.assegnazioneBadge = tf.getDAssegnazioneBadge() != null ? 
				tf.getDAssegnazioneBadge().getSequIdAssegnazioneBadge() : null;
		this.causale = tf.getDCausaleTimbraturaPk() != null ?
				tf.getDCausaleTimbraturaPk().getSequIdCausaleTimbraturaPk() : null;
		this.lettoreBadge = tf.getDLettoreBadgeFisico() != null ? 
				tf.getDLettoreBadgeFisico().getSequIdLettoreBadgeFisico() : null;
		this.testinaLettoreBadge = tf.getDTestinaLettoreBadge() != null ? 
				tf.getDTestinaLettoreBadge().getSequIdTestinaLettoreBadge() : null;
	}
}