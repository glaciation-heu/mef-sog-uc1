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
import lombok.ToString;


/**
 * The persistent class for the D_CAUSALE_TIMBRATURA_H database table.
 * 
 */
@Entity
@Data
@Table(name="D_CAUSALE_TIMBRATURA_H")
@NamedQuery(name="DCausaleTimbraturaH.findAll", query="SELECT d FROM DCausaleTimbraturaH d")
public class DCausaleTimbraturaH implements BaseDomainTimed {

	@Id
	@SequenceGenerator(name="D_CAUSALE_TIMBRATURA_H_SEQUIDCAUSALETIMBRATURAH_GENERATOR", allocationSize = 1, sequenceName="D_CAUSALE_TIMBRATURA_H_SE")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="D_CAUSALE_TIMBRATURA_H_SEQUIDCAUSALETIMBRATURAH_GENERATOR")
	@Column(name="SEQU_ID_CAUSALE_TIMBRATURA_H")
	private Long sequIdCausaleTimbraturaH;

	@Column(name="CODI_CODICE")
	private String codiCodice;

	@Column(name="DESC_DESCRIZIONE")
	private String descDescrizione;

	@Column(name="FK_AMMINISTRAZIONE")
	private Long vAmministrazioniPk;
	
	//bi-directional many-to-one association to DCausaleTimbraturaPk
	@ManyToOne(cascade = {CascadeType.MERGE} )
	@ToString.Exclude
	@JoinColumn(name="FK_CAUSALE_TIMBRATURA_PK")
	private DCausaleTimbraturaPk dCausaleTimbraturaPk;

	/** Data fine del record: rappresent */
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataFineValidita;
	
	/** Data inizio validità del record dataInizioValidita */
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataInizioValidita;
}