package it.mef.tm.elaboration.timb.db.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;


/**
 * The persistent class for the D_LETTORE_BADGE_FISICO database table.
 * 
 */
@Entity
@Data
@NoArgsConstructor
@Table(name="D_LETTORE_BADGE_FISICO")
@NamedQuery(name="DLettoreBadgeFisico.findAll", query="SELECT d FROM DLettoreBadgeFisico d")
public class DLettoreBadgeFisico implements BaseDomainTimed {

	@Id
	@SequenceGenerator(name="D_LETTORE_BADGE_FISICO_SEQUIDLETTOREBADGEFISICO_GENERATOR", allocationSize = 1, sequenceName="D_LETTORE_BADGE_FISICO_SE")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="D_LETTORE_BADGE_FISICO_SEQUIDLETTOREBADGEFISICO_GENERATOR")
	@Column(name="SEQU_ID_LETTORE_BADGE_FISICO")
	private Long sequIdLettoreBadgeFisico;

	@Column(name="CODI_CODICE")
	private String codiCodice;

	@Column(name="CODI_CODICE_CONCENTRATORE")
	private String codiCodiceConcentratore;

	@Column(name="DESC_DESCRIZIONE")
	private String descDescrizione;
	
	@Column(name="FK_AMMINISTRAZIONE")
	private Long vAmministrazioniPk;

	@Column(name="TEXT_NOTE")
	private String textNote;

	@Column(name="FLAG_ABIL_CONTABILIZ_PASTO")
	private String flagAbilContabilizPasto;
	
	@Column(name="FLAG_ABIL_RILEVAZIONE_IN_OUT")
	private String flagAbilRilevazioneInOut;
	
	/** Data inizio validità del record dataInizioValidita */
	@Column(name="DATA_INIZIO_RECORD")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataInizioValidita;

	/** Data fine del record: rappresent */
	@Column(name="DATA_FINE_RECORD")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataFineValidita;
	
	//bi-directional many-to-one association to DGruppoLettoriPerCausali
	@ManyToOne
	@ToString.Exclude
	@JoinColumn(name="FK_GRUPPO_LETTORI_PER_CAUSALI")
	public DGruppoLettoriPerCausali dGruppoLettoriPerCausali;

	//bi-directional many-to-one association to DTestinaLettoreBadge
	@OneToMany(mappedBy="dLettoreBadgeFisico")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<DTestinaLettoreBadge> dTestinaLettoreBadges;

}