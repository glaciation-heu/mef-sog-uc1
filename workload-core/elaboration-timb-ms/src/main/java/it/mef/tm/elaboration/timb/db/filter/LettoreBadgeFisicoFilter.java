package it.mef.tm.elaboration.timb.db.filter;

import static it.mef.tm.elaboration.timb.util.Constants.CAMPO_CODICE;
import static it.mef.tm.elaboration.timb.util.Constants.V_AMMINISTRAZIONI_PK;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import it.mef.tm.elaboration.timb.db.entities.DLettoreBadgeFisico;
import it.mef.tm.elaboration.timb.db.entities.DTestinaLettoreBadge;
import it.mef.tm.elaboration.timb.util.StringUtility;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * LettoreBadgeFisicoFilter.java
 * filtro per lettore badge
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  23 mag 2024
 * @Description prima versione
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class LettoreBadgeFisicoFilter extends BaseDomainFilter<DLettoreBadgeFisico> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String codiCodice;
	private Long fkAmministrazioneId;
	private String codiTestina;
	
	/**
	 * override del metodo di aggiunta filtri
	 * 
	 * @param builder CriteriaBuilder
	 * @param root    Root<> di hibernate
	 * @return filtri
	 */
	@Override
	public List<Predicate> getCustomPredicate(CriteriaBuilder builder, Root<DLettoreBadgeFisico> root) {

		List<Predicate> predicates = new ArrayList<>();
		aggiungiFiltriRicerca(predicates, root, builder);
		return predicates;
	}

	/**
	 * metodo per settare i filtri dei codici
	 * 
	 * @param predicates 
	 * @param root
	 * @param builder
	 * @param pattern
	 */
	private void setFiltroCodice(List<Predicate> predicates, Root<DLettoreBadgeFisico> root, CriteriaBuilder builder) {
		if (this.getCodiCodice() != null && !this.getCodiCodice().isEmpty()) {
			predicates.add(builder.equal(builder.upper(root.get(CAMPO_CODICE)), this.getCodiCodice()));
		}
	}

	/**
	 * metodo per settare i filtri amministrazione
	 * 
	 * @param predicates 
	 * @param root
	 * @param builder
	 */
	private void setFiltroAmministrazioni(List<Predicate> predicates, Root<DLettoreBadgeFisico> root,
			CriteriaBuilder builder) {
		if (this.getFkAmministrazioneId() != null) {
			predicates.add(builder.equal(root.get(V_AMMINISTRAZIONI_PK), this.getFkAmministrazioneId()));
		}
	}

	/**
	 * metodo per settare i filtri ricerca
	 * 
	 * @param predicates 
	 * @param root
	 * @param builder
	 */
	private void aggiungiFiltriRicerca(List<Predicate> predicates, Root<DLettoreBadgeFisico> root,
			CriteriaBuilder builder) {

		setFiltroCodice(predicates, root, builder);
		setFiltroAmministrazioni(predicates, root, builder);
		setFiltroTestina(predicates, builder, root);
	}

	/**
	 * metodo per settare i filtri testina
	 * 
	 * @param predicates 
	 * @param builder
	 * @param root
	 */
	private void setFiltroTestina(List<Predicate> predicates, CriteriaBuilder builder,
			Root<DLettoreBadgeFisico> root) {
		if(!StringUtility.isNullOrEmpty(this.codiTestina)) {
			Join<DLettoreBadgeFisico, DTestinaLettoreBadge> dLettoriTestinaJoin = root
					.join("dTestinaLettoreBadges", JoinType.INNER);
			predicates.add(builder.equal(dLettoriTestinaJoin.get(CAMPO_CODICE), this.codiTestina));
		}
	}
}
