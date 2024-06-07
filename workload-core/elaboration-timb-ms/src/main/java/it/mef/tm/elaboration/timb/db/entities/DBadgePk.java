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
 * The persistent class for the D_BADGE_PK database table.
 * 
 */
@Data
@Entity
@Table(name="D_BADGE_PK")
@NamedQuery(name="DBadgePk.findAll", query="SELECT d FROM DBadgePk d")
public class DBadgePk {

	@Id
	@SequenceGenerator(name="D_BADGE_PK_SEQUIDBADGEPK_GENERATOR", allocationSize = 1, sequenceName="D_BADGE_PK_SE")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="D_BADGE_PK_SEQUIDBADGEPK_GENERATOR")
	@Column(name="SEQU_ID_BADGE_PK")
	private Long sequIdBadgePk;

	//bi-directional many-to-one association to DBadgeH , cascade = CascadeType.PERSIST
	@OneToMany(mappedBy="dBadgePk")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<DBadgeH> dBadgeHs;

	//bi-directional many-to-one association to DBadgeTecnologiaTestinaH
	@OneToMany(mappedBy="dBadgePk")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<DBadgeTecnologiaTestinaH> dBadgeTecnologiaTestinaHs;
	
}