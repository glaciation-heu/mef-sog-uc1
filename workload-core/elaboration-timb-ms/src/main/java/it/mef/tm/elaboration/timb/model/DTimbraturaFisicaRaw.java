package it.mef.tm.elaboration.timb.model;

import java.util.List;

import lombok.Data;

/**
 * DTimbraturaFisicaRaw.java
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  17 mag 2024
 * @Description prima versione
 */
@Data
public class DTimbraturaFisicaRaw {

	private String descCodiceBadge;

	private String descCodiceCausale;

	private String descCodiceConcentratore;

	private String descCodiceLettore;

	private String descCodiceTestina;

	private String descCodiceTimbratura;

	private String descData;

	private String descOra;

	private String descVerso;

	private List<DTimbraturaFisica> dTimbraturaFisicas;

}