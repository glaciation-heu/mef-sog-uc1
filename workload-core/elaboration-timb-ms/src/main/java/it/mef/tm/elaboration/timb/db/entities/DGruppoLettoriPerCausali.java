package it.mef.tm.elaboration.timb.db.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * The persistent class for the D_GRUPPO_LETT_PER_CAUSALI database table.
 * 
 */
@Entity
@Data
@Table(name="D_GRUPPO_LETT_PER_CAUSALI")
@NamedQuery(name="DGruppoLettoriPerCausali.findAll", query="SELECT d FROM DGruppoLettoriPerCausali d")
public class DGruppoLettoriPerCausali implements BaseDomainTimed {

	@Id
	@SequenceGenerator(name="D_GRUPPO_LETTORI_PER_CAUSALI_SEQUIDGRUPPOLETTPERCAUS_GENERATOR", allocationSize = 1, sequenceName="D_GRUPPO_LETT_PER_CAUS_SE")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="D_GRUPPO_LETTORI_PER_CAUSALI_SEQUIDGRUPPOLETTPERCAUS_GENERATOR")
	@Column(name="SEQU_ID_GRUPPO_LETT_PER_CAUS")
	private Long sequIdGruppoLettPerCaus;

	@Column(name="CODI_CODICE")
	private String codiCodice;

	@Column(name="DESC_DESCRIZIONE")
	private String descDescrizione;

	@Column(name="FK_AMMINISTRAZIONE")
	private Long fkAmministrazioneId;

	//bi-directional many-to-one association to DCausaleTimbrGruppoLettH
	@OneToMany(mappedBy="dGruppoLettoriPerCausali")
	@ToString.Exclude
	private List<DCausaleTimbrGruppoLettH> dCausaleTimbrGruppoLettHs;

	//bi-directional many-to-one association to DLettoreBadgeFisico
	@OneToMany(mappedBy="dGruppoLettoriPerCausali")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<DLettoreBadgeFisico> dLettoreBadgeFisicos;
	
	/** Data inizio validità del record dataInizioValidita */
	@Column(name="DATA_INIZIO_RECORD")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataInizioValidita;

	/** Data fine del record: rappresent */
	@Column(name="DATA_FINE_RECORD")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataFineValidita;

}