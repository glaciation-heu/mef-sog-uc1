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
 * The persistent class for the D_ASSEGNAZIONE_BADGE database table.
 * 
 */
@Entity
@Data
@Table(name="D_ASSEGNAZIONE_BADGE")
@NamedQuery(name="DAssegnazioneBadge.findAll", query="SELECT d FROM DAssegnazioneBadge d")
public class DAssegnazioneBadge implements BaseDomainTimed {


	@Id
	@SequenceGenerator(name="D_ASSEGNAZIONE_BADGE_SEQUIDASSEGNAZIONEBADGE_GENERATOR", allocationSize = 1, sequenceName="D_ASSEGNAZIONE_BADGE_SE")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="D_ASSEGNAZIONE_BADGE_SEQUIDASSEGNAZIONEBADGE_GENERATOR")
	@Column(name="SEQU_ID_ASSEGNAZIONE_BADGE")
	private Long sequIdAssegnazioneBadge;

	//bi-directional many-to-one association to DBadgePk
	@ManyToOne
	@ToString.Exclude
	@JoinColumn(name="FK_BADGE_PK_SOSTITUITO")
	private DBadgePk dBadgePkSostituito;

	//bi-directional many-to-one association to DBadgePk
	@ManyToOne
	@ToString.Exclude
	@JoinColumn(name="FK_BADGE_PK")
	private DBadgePk dBadgePk;
	
	/** Data inizio validità del record dataInizioValidita */
	@Column(name="DATA_INIZIO_RECORD")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataInizioValidita;

	/** Data fine del record: rappresent */
	@Column(name="DATA_FINE_RECORD")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataFineValidita;
		
}