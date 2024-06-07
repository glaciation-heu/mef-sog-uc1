package it.mef.tm.elaboration.timb.model;

import java.util.Date;

import it.mef.tm.elaboration.timb.db.entities.DAssegnazioneBadge;
import it.mef.tm.elaboration.timb.db.entities.DCausaleTimbraturaPk;
import it.mef.tm.elaboration.timb.db.entities.DLettoreBadgeFisico;
import it.mef.tm.elaboration.timb.db.entities.DTestinaLettoreBadge;
import lombok.Data;

/**
 * DTimbraturaFisica.java
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  17 mag 2024
 * @Description prima versione
 */
@Data
public class DTimbraturaFisica {

	private Date dttmDataOra;

	private String statVerso;

	private DAssegnazioneBadge dAssegnazioneBadge;

	private DCausaleTimbraturaPk dCausaleTimbraturaPk;

	private DLettoreBadgeFisico dLettoreBadgeFisico;

	private DTestinaLettoreBadge dTestinaLettoreBadge;

	private DTimbraturaFisicaRaw dTimbraturaFisicaRaw;
	
}