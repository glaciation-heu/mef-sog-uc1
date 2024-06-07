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
 * The persistent class for the D_CAUSA_TIMBR_GRUP_LETT_H database table.
 * 
 */
@Entity
@Data
@Table(name="D_CAUSA_TIMBR_GRUP_LETT_H")
@NamedQuery(name="DCausaleTimbrGruppoLettH.findAll", query="SELECT d FROM DCausaleTimbrGruppoLettH d")
public class DCausaleTimbrGruppoLettH implements BaseDomainTimed {

	@Id
	@SequenceGenerator(name="D_CAUSALE_TIMBR_GRUPPO_LETT_H_SEQUIDCAUSTIMBRGRUPLETTH_GENERATOR",
			  allocationSize = 1, sequenceName="D_CAUSALE_TIMBR_GRUP_LETT_H_SE")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="D_CAUSALE_TIMBR_GRUPPO_LETT_H_SEQUIDCAUSTIMBRGRUPLETTH_GENERATOR")
	@Column(name="SEQU_ID_CAUS_TIMBR_GRUP_LETT_H")
	private Long sequIdCausTimbrGrupLettH;
	
	//bi-directional many-to-one association to DCausaleTimbraturaPk
	@ManyToOne
	@JoinColumn(name="FK_CAUSALE_TIMBRATURA_PK")
	private DCausaleTimbraturaPk dCausaleTimbraturaPk;

	//bi-directional many-to-one association to DGruppoLettoriPerCausali
	@ManyToOne
	@JoinColumn(name="FK_GRUPPO_LETTORI_PER_CAUSALI")
	private DGruppoLettoriPerCausali dGruppoLettoriPerCausali;

	/** Data fine del record: rappresent */
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataFineValidita;
	
	/** Data inizio validità del record dataInizioValidita */
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataInizioValidita;

}