package it.mef.tm.elaboration.timb.db.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;


/**
 * The persistent class for the D_TRACCIATO_TIMBRATURE_H database table.
 * 
 */
@Entity
@Data
@Inheritance(strategy= InheritanceType.JOINED)
@Table(name="D_TRACCIATO_TIMBRATURE_H")
@NamedQuery(name="DTracciatoTimbratureH.findAll", query="SELECT d FROM DTracciatoTimbratureH d")
public class DTracciatoTimbratureH implements BaseDomainTimed {

	@Id
	@SequenceGenerator(name="D_TRACCIATO_TIMBRATURE_H_SEQUIDTRACCIATOTIMBRATUREH_GENERATOR", allocationSize = 1, sequenceName="D_TRACCIATO_TIMBRATURE_H_SE")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="D_TRACCIATO_TIMBRATURE_H_SEQUIDTRACCIATOTIMBRATUREH_GENERATOR")
	@Column(name="SEQU_ID_TRACCIATO_TIMBRATURE_H")
	private Long sequIdTracciatoTimbratureH;

	@Column(name="CODI_CODICE")
	private String codiCodice;

	@Column(name="DESC_DESCRIZIONE")
	private String descDescrizione;

	@Column(name="DESC_RAPPRESENTAZ_VERSO_IN")
	private String descRappresentazVersoIn;

	@Column(name="DESC_RAPPRESENTAZ_VERSO_OUT")
	private String descRappresentazVersoOut;

	@Column(name="FK_AMMINISTRAZIONE")
	private Long vAmministrazioniPk;

	@Column(name="FLAG_CODICE_BDG_PER_TECN_TEST")
	private String flagCodiceBdgPerTecnTest;

	@Column(name="STAT_FORMATO_DATA")
	private String statFormatoData;

	@Column(name="STAT_FORMATO_ORA")
	private String statFormatoOra;

	@Column(name="TEXT_NOTE")
	private String textNote;

	/** Data fine del record: rappresent */
	@Temporal(TemporalType.TIMESTAMP)
	protected Date dataFineValidita;
	
	/** Data inizio validità del record dataInizioValidita */
	@Temporal(TemporalType.TIMESTAMP)
	protected Date dataInizioValidita;

}