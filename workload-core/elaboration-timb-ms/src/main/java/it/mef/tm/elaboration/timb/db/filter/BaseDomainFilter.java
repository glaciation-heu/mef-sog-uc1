package it.mef.tm.elaboration.timb.db.filter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import it.mef.tm.elaboration.timb.util.DateUtility;
import lombok.Data;

/**
 * BaseDomainFilter.java
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  23 mag 2024
 * @Description prima versione
 */
@Data
public abstract class BaseDomainFilter<T> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Date dataFineValidita;
	
	private Date dataInizioValidita;
		
	private boolean intervalloTemporale;
	/**
	 * Creazione dei predicati relativi alla classe che estende BaseDomainFilter
	 * @return
	 **/
	public abstract List<Predicate> getCustomPredicate(CriteriaBuilder builder, Root<T> root);

	// Un valore null in input corrisponde ad una data specifica sul DB (31/12/9999)
	public void setDataFineValidita(Date dataFineValidita) {
		this.dataFineValidita = dataFineValidita != null ? dataFineValidita : DateUtility.getEndPeriodDate();
	}
}
