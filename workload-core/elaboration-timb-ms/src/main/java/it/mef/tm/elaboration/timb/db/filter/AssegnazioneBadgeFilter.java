package it.mef.tm.elaboration.timb.db.filter;


import static it.mef.tm.elaboration.timb.util.Constants.BADGE_PK_VS_H;
import static it.mef.tm.elaboration.timb.util.Constants.CAMPO_CODICE;
import static it.mef.tm.elaboration.timb.util.Constants.DATA_FINE_VALID;
import static it.mef.tm.elaboration.timb.util.Constants.DATA_INIT_VALID;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import it.mef.tm.elaboration.timb.db.entities.DAssegnazioneBadge;
import it.mef.tm.elaboration.timb.db.entities.DBadgeH;
import it.mef.tm.elaboration.timb.db.entities.DBadgePk;
import it.mef.tm.elaboration.timb.db.entities.DBadgeTecnologiaTestinaH;
import it.mef.tm.elaboration.timb.util.StringUtility;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AssegnazioneBadgeFilter.java
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  31 mag 2024
 * @Description prima versione
 */
@Data 
@EqualsAndHashCode(callSuper = true)
public class AssegnazioneBadgeFilter extends BaseDomainFilter<DAssegnazioneBadge>{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String SEQU_ID_AMMINISTRAZIONE_PK = "vAmministrazioniPk";
	
	
	/** identificativo amministrazione */
	private String codAmministrazione;
	
	private String codBadgeEqual;

	private Boolean sostitutivo;

	/** codice badge per timbratura*/
	private String codBadgeTimbr;
	
	// Join necessarie per il recupero dei dati
	private Join<DAssegnazioneBadge, DBadgePk> joinBadgePk;
	private Join<DAssegnazioneBadge, DBadgePk> joinBadgeSostitutivoPk;
	private Join<DBadgePk, DBadgeH> joinBadgeH;
	private Join<DBadgePk, DBadgeH> joinBadgeSostitutivoH;
	
	// Join necessarie solo in caso vangano impostati i filtri
	private Join<DBadgePk, DBadgeTecnologiaTestinaH> joinTeconologiaBadge;
	private Join<DBadgePk, DBadgeTecnologiaTestinaH> joinTeconologiaBadgeSostitutivo;
	
	@Override
	public List<Predicate> getCustomPredicate(CriteriaBuilder builder, Root<DAssegnazioneBadge> root) {
		List<Predicate> predicates = new ArrayList<>();
				
		this.initStandardJoin(builder, root);

		if (this.codAmministrazione != null) {
			Predicate ammStandard = builder.equal(this.joinBadgeH.get(SEQU_ID_AMMINISTRAZIONE_PK), Long.parseLong(this.codAmministrazione));
			Predicate ammSostitutiva =  builder.equal(this.joinBadgeSostitutivoH.get(SEQU_ID_AMMINISTRAZIONE_PK), Long.parseLong(this.codAmministrazione));
			predicates.add(builder.or(ammStandard, ammSostitutiva));
		}

		if(this.codBadgeTimbr != null) {
			Join<DBadgePk, DBadgeTecnologiaTestinaH> dbadgeTecnTestHStand= commonJoinTecnologiaTestinaStandard(predicates, builder, root);
			Join<DBadgePk, DBadgeTecnologiaTestinaH> dbadgeTecnTestHSost = commonJoinTecnologiaTestinaSostitutivo(predicates, builder, root);
			setCodBadgeTimbrPredicate(predicates, builder, root, dbadgeTecnTestHStand, dbadgeTecnTestHSost);
		}

		setCodBadgeEqualPredicate(predicates, builder, root);
		
		return predicates;
	}
	
	private void initStandardJoin(CriteriaBuilder builder, Root<DAssegnazioneBadge> root) {
		this.joinBadgePk = root.join("dBadgePk", JoinType.LEFT);
		if (this.getSostitutivo() == null || this.getSostitutivo() == false) {
			this.joinBadgePk.on(builder.isNull(root.<DBadgePk>get("dBadgePkSostituito")));
		}
		
		this.joinBadgeSostitutivoPk = root.join("dBadgePkSostituito", JoinType.LEFT);
		
		this.joinBadgeH = this.joinBadgePk.join(BADGE_PK_VS_H, JoinType.LEFT);
		Predicate dataInizio = builder.greaterThanOrEqualTo(this.joinBadgeH.<Date>get(DATA_FINE_VALID), root.<Date>get(DATA_FINE_VALID));
		Predicate dataFine = builder.lessThanOrEqualTo(this.joinBadgeH.<Date>get(DATA_INIT_VALID), root.<Date>get(DATA_FINE_VALID));	
		this.joinBadgeH.on(builder.and(dataInizio, dataFine));
		
		this.joinBadgeSostitutivoH = this.joinBadgeSostitutivoPk.join(BADGE_PK_VS_H, JoinType.LEFT);
		Predicate dataInizioSostitutivo = builder.greaterThanOrEqualTo(this.joinBadgeSostitutivoH.<Date>get(DATA_FINE_VALID), root.<Date>get(DATA_FINE_VALID));
		Predicate dataFineSostitutivo = builder.lessThanOrEqualTo(this.joinBadgeSostitutivoH.<Date>get(DATA_INIT_VALID), root.<Date>get(DATA_FINE_VALID));	
		this.joinBadgeSostitutivoH.on(builder.and(dataInizioSostitutivo, dataFineSostitutivo));
	}
	
	/**
	 * Metodo per settare il codice Badge necessario per fixare la segnalazione HGH0000008112
	 * @param predicates
	 * @param builder
	 * @param root
	 */
	private void setCodBadgeEqualPredicate(List<Predicate> predicates, CriteriaBuilder builder, Root<DAssegnazioneBadge> root) {
		if(!StringUtility.isNullOrEmpty(codBadgeEqual)) {
		predicates.add(builder.equal((this.joinBadgeH.<String>get(CAMPO_CODICE)), codBadgeEqual));
		}
	}

	/**
	 * metodo per settare i filtri codice tibratura
	 * 
	 * @param predicates 
	 * @param builder
	 * @param root
	 * @param badgeTecnTestHJoinStand
	 * @param badgeTecnTestHJoinSost
	 */
	private void setCodBadgeTimbrPredicate(List<Predicate> predicates, CriteriaBuilder builder, Root<DAssegnazioneBadge> root, Join<DBadgePk, DBadgeTecnologiaTestinaH> badgeTecnTestHJoinStand, Join<DBadgePk, DBadgeTecnologiaTestinaH> badgeTecnTestHJoinSost) {

		predicates.add(builder.or(codBadgeTimbrAndPredicate(builder, badgeTecnTestHJoinStand, root), codBadgeTimbrEqualPredicate(builder, badgeTecnTestHJoinSost)));

	}

	/**
	 * metodo per settare i filtri timbrature
	 * 
	 * @param builder 
	 * @param dBadgeTecnTestHStand
	 * @param root
	 */
	private Predicate codBadgeTimbrAndPredicate(CriteriaBuilder builder, Join<DBadgePk, DBadgeTecnologiaTestinaH> dBadgeTecnTestHStand, Root<DAssegnazioneBadge> root) {
		return builder.and(codBadgeTimbrEqualPredicate(builder, dBadgeTecnTestHStand), codBadgeNullSostPredicate(builder, root));
	}

	/**
	 * metodo per settare altri filtri timbrature
	 * 
	 * @param builder 
	 * @param badgeTecnTestHJoin
	 */
	private Predicate codBadgeTimbrEqualPredicate(CriteriaBuilder builder, Join<DBadgePk, DBadgeTecnologiaTestinaH> badgeTecnTestHJoin) {
		return builder.equal(badgeTecnTestHJoin.get("codiCodiceBadgePerTimbr"), this.codBadgeTimbr);
	}

	/**
	 * metodo per settare badge nullo
	 * 
	 * @param builder 
	 * @param root
	 */
	private Predicate codBadgeNullSostPredicate(CriteriaBuilder builder, Root<DAssegnazioneBadge> root) {
		return builder.isNull(root.<DBadgePk>get("dBadgePkSostituito"));
	}

	/**
	 * metodo per settare common join testina
	 * 
	 * @param predicates
	 * @param builder 
	 * @param root
	 */
	private Join<DBadgePk, DBadgeTecnologiaTestinaH> commonJoinTecnologiaTestinaStandard (List<Predicate> predicates, CriteriaBuilder builder, Root<DAssegnazioneBadge> root) {

		if (this.joinTeconologiaBadge == null) {
			this.joinTeconologiaBadge = this.joinBadgePk.join("dBadgeTecnologiaTestinaHs", JoinType.LEFT);
		}
		
		return this.joinTeconologiaBadge;
	}

	/**
	 * metodo per settare common join tecnologia
	 * 
	 * @param predicates
	 * @param builder 
	 * @param root
	 */
	private Join<DBadgePk, DBadgeTecnologiaTestinaH> commonJoinTecnologiaTestinaSostitutivo (List<Predicate> predicates, CriteriaBuilder builder, Root<DAssegnazioneBadge> root) {

		if (this.joinTeconologiaBadge == null) {
			this.joinTeconologiaBadge = this.joinBadgeSostitutivoPk.join("dBadgeTecnologiaTestinaHs", JoinType.LEFT);
		}
		
		return this.joinTeconologiaBadge;

	}
}
