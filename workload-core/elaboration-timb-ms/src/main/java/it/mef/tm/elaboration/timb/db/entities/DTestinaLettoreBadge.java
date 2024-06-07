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


/**
 * The persistent class for the D_TESTINA_LETTORE_BADGE database table.
 * 
 */
@Entity
@Table(name="D_TESTINA_LETTORE_BADGE")
@Data
@NamedQuery(name="DTestinaLettoreBadge.findAll", query="SELECT d FROM DTestinaLettoreBadge d")
public class DTestinaLettoreBadge implements BaseDomainTimed {

	@Id
	@SequenceGenerator(name="D_TESTINA_LETTORE_BADGE_SEQUIDTESTINALETTOREBADGE_GENERATOR", allocationSize = 1, sequenceName="D_TESTINA_LETTORE_BADGE_SE")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="D_TESTINA_LETTORE_BADGE_SEQUIDTESTINALETTOREBADGE_GENERATOR")
	@Column(name="SEQU_ID_TESTINA_LETTORE_BADGE")
	private Long sequIdTestinaLettoreBadge;

	@Column(name="CODI_CODICE")
	private String codiCodice;

	@Column(name="STAT_VERSO")
	private String statVerso;

	//bi-directional many-to-one association to DLettoreBadgeFisico
	@ManyToOne
	@JoinColumn(name="FK_LETTORE_BADGE_FISICO")
	private DLettoreBadgeFisico dLettoreBadgeFisico;
	
	/** Data inizio validità del record dataInizioValidita */
	@Column(name="DATA_INIZIO_RECORD")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataInizioValidita;

	/** Data fine del record: rappresent */
	@Column(name="DATA_FINE_RECORD")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataFineValidita;
}