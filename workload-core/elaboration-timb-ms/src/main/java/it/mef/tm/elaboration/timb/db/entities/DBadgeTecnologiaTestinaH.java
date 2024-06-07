package it.mef.tm.elaboration.timb.db.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;
import lombok.ToString;


/**
 * The persistent class for the D_BADGE_TECNOLOG_TESTINA_H database table.
 * 
 */
@Entity
@Data
@Table(name="D_BADGE_TECNOLOG_TESTINA_H")
@NamedQuery(name="DBadgeTecnologiaTestinaH.findAll", query="SELECT d FROM DBadgeTecnologiaTestinaH d")
public class DBadgeTecnologiaTestinaH implements BaseDomainTimed {

	@Id
	@SequenceGenerator(name="D_BADGE_TECNOLOGIA_TESTINA_H_SEQUIDBADGETECNTESTH_GENERATOR",
			  allocationSize = 1, sequenceName="D_BADGE_TECNOLOGIA_TEST_H_SE")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="D_BADGE_TECNOLOGIA_TESTINA_H_SEQUIDBADGETECNTESTH_GENERATOR")
	@Column(name="SEQU_ID_BADGE_TECN_TEST_H")
	private Long sequIdBadgeTecnTestH;

	@Column(name="CODI_CODICE_BADGE_PER_TIMBR")
	private String codiCodiceBadgePerTimbr;

	//bi-directional many-to-one association to DBadgePk
	@ManyToOne
	@ToString.Exclude
	@JoinColumn(name="FK_BADGE_PK")
	private DBadgePk dBadgePk;

	/** Data fine del record: rappresent */
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataFineValidita;
	
	/** Data inizio validità del record dataInizioValidita */
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataInizioValidita;
}