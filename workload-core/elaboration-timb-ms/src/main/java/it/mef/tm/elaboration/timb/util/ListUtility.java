package it.mef.tm.elaboration.timb.util;

import java.util.Date;
import java.util.List;

import it.mef.tm.elaboration.timb.db.entities.BaseDomainTimed;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * ListUtility.java
 * classe di util per le liste
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  4 apr 2024
 * @Description prima versione
 */
@NoArgsConstructor(access = AccessLevel.NONE)
public class ListUtility {
	
	/**
	 * Metodo che verifica se la stringa in input
	 * sia non valorizzata oppure vuota
	 * @param s
	 * @return
	 */
	public static <T> boolean isNullOrEmpty(List<T> lista) {
		return lista == null || lista.isEmpty();
	}

	/**
	 * Metodo che controlla se negli elementi della lista periodi vi sia un elemento che comprende la data passata in input
	 * @param data
	 * @param list
	 * @return H extends BaseDomainTimed
	 */
	public static <H extends BaseDomainTimed> H getVersionIncludingDate(Date data, List<H> list) {
		return list.stream()
				.filter(item -> (DateUtility.compare(data, item.getDataInizioValidita())>=0) && (DateUtility.compare(data, item.getDataFineValidita())<=0))
				.findAny().orElse(null);
	}
}
