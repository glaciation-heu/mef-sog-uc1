package it.mef.tm.elaboration.timb.db.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * The persistent class for the D_TRACC_TIMBR_XML_H database table.
 * 
 */
@Entity
@Data
@EqualsAndHashCode(callSuper=true)
@Table(name="D_TRACC_TIMBR_XML_H")
@NamedQuery(name="DTraccTimbrXmlH.findAll", query="SELECT d FROM DTraccTimbrXmlH d")
@PrimaryKeyJoinColumn(name="FK_TRACCIATO_TIMBRATURE_H")
public class DTraccTimbrXmlH extends DTracciatoTimbratureH {

	@Column(name="DESC_TAG_CODICE_BADGE")
	private String descTagCodiceBadge;

	@Column(name="DESC_TAG_CODICE_CAUSALE")
	private String descTagCodiceCausale;

	@Column(name="DESC_TAG_CODICE_CONCENTR")
	private String descTagCodiceConcentr;

	@Column(name="DESC_TAG_CODICE_LETTORE")
	private String descTagCodiceLettore;

	@Column(name="DESC_TAG_CODICE_TESTINA")
	private String descTagCodiceTestina;

	@Column(name="DESC_TAG_CODICE_TIMBRATURA")
	private String descTagCodiceTimbratura;

	@Column(name="DESC_TAG_DATA")
	private String descTagData;

	@Column(name="DESC_TAG_ORA")
	private String descTagOra;

	@Column(name="DESC_TAG_TIMBRATURA")
	private String descTagTimbratura;

	@Column(name="DESC_TAG_VERSO")
	private String descTagVerso;
}