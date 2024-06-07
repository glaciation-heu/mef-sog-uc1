package it.mef.tm.elaboration.timb.db.filter;



import static it.mef.tm.elaboration.timb.util.Constants.CAMPO_CODICE;
import static it.mef.tm.elaboration.timb.util.Constants.V_AMMINISTRAZIONI_PK;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import it.mef.tm.elaboration.timb.db.entities.DCausaleTimbraturaH;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * CausaleTimbratureFilter.java
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  31 mag 2024
 * @Description prima versione
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CausaleTimbratureFilter extends BaseDomainFilter<DCausaleTimbraturaH> {
	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	private String codice;

	private String codiceAmministrazione;
	
	@Override
	public List<Predicate> getCustomPredicate(CriteriaBuilder builder, Root<DCausaleTimbraturaH> root) {

		List<Predicate> predicati = new ArrayList<>();
		setCodicePredicate(predicati, builder, root);
		setAmministrazionePredicate(predicati, builder, root);
		return predicati;
	}
	
	private void setCodicePredicate(List<Predicate> predicati, CriteriaBuilder builder, Root<?> root) {

		if (codice != null) {
			predicati.add(builder.equal(builder.upper(root.<String>get(CAMPO_CODICE)), codice));
		}		
	}

	private void setAmministrazionePredicate(List<Predicate> predicati, CriteriaBuilder builder, Root<?> root) {

		if (codiceAmministrazione != null) {			
			Long codAmm = Long.parseLong(codiceAmministrazione);
			predicati.add(builder.equal(root.get(V_AMMINISTRAZIONI_PK), codAmm));
		}
	}
	
}
