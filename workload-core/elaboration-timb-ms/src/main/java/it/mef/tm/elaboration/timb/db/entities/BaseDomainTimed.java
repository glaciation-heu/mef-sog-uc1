package it.mef.tm.elaboration.timb.db.entities;

import java.util.Date;

/**
 * BaseDomainTimed.java
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  17 mag 2024
 * @Description prima versione
 */
public interface BaseDomainTimed {

	public Date getDataFineValidita();
	
	public Date getDataInizioValidita();
}
