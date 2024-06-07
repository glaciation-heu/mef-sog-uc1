package it.mef.tm.elaboration.timb.db.entities;

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

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * The persistent class for the D_CAUSALE_TIMBRATURA_PK database table.
 * 
 */
@Entity
@Data
@Table(name="D_CAUSALE_TIMBRATURA_PK")
@NamedQuery(name="DCausaleTimbraturaPk.findAll", query="SELECT d FROM DCausaleTimbraturaPk d")
public class DCausaleTimbraturaPk {

	@Id
	@SequenceGenerator(name="D_CAUSALE_TIMBRATURA_PK_SEQUIDCAUSALETIMBRATURAPK_GENERATOR", allocationSize = 1, sequenceName="D_CAUSALE_TIMBRATURA_PK_SE")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="D_CAUSALE_TIMBRATURA_PK_SEQUIDCAUSALETIMBRATURAPK_GENERATOR")
	@Column(name="SEQU_ID_CAUSALE_TIMBRATURA_PK")
	private Long sequIdCausaleTimbraturaPk;

	//bi-directional many-to-one association to DCausaleTimbraturaH
	@OneToMany(mappedBy="dCausaleTimbraturaPk")
	@EqualsAndHashCode.Exclude
	private List<DCausaleTimbraturaH> dCausaleTimbraturaHs;

	//bi-directional many-to-one association to DCausaleTimbrGruppoLettH
	@OneToMany(mappedBy="dCausaleTimbraturaPk")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<DCausaleTimbrGruppoLettH> dCausaleTimbrGruppoLettHs;

}