package it.mef.tm.elaboration.timb.db.entities;

import java.util.Date;

import javax.persistence.CascadeType;
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

/**
 * The persistent class for the D_BADGE_H database table.
 * 
 */
@Entity
@Data
@Table(name="D_BADGE_H")
@NamedQuery(name="DBadgeH.findAll", query="SELECT d FROM DBadgeH d")
public class DBadgeH implements BaseDomainTimed {

	@Id
	@SequenceGenerator(name="D_BADGE_H_SEQUIDBADGEH_GENERATOR", allocationSize = 1, sequenceName="D_BADGE_H_SE")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="D_BADGE_H_SEQUIDBADGEH_GENERATOR")
	@Column(name="SEQU_ID_BADGE_H")
	private Long sequIdBadgeH;

	@Column(name="CODI_CODICE")
	private String codiCodice;

	@Column(name="FK_AMMINISTRAZIONE")
	private Long vAmministrazioniPk;
	
	//bi-directional many-to-one association to DBadgePk
	@ManyToOne(cascade = {CascadeType.MERGE} )
	@JoinColumn(name="FK_BADGE_PK")
	private DBadgePk dBadgePk;

	/** Data fine del record: rappresent */
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataFineValidita;
	
	/** Data inizio validità del record dataInizioValidita */
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataInizioValidita;
	
}