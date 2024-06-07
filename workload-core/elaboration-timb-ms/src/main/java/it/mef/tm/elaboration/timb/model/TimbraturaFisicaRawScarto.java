package it.mef.tm.elaboration.timb.model;

import org.springframework.beans.BeanUtils;

import it.mef.tm.elaboration.timb.exception.NotRollBackException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * TimbraturaFisicaRawScarto.java
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  29 mag 2024
 * @Description prima versione
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class TimbraturaFisicaRawScarto extends DTimbraturaFisicaRaw 
{
	private NotRollBackException eccezioneScarto;
	
	/**
	 * costruttore
	 * 
	 * @param dFileTimbrature
	 * @param amministrazione
	 */
	public TimbraturaFisicaRawScarto(DTimbraturaFisicaRaw dTimbraturaFisicaRaw) {
		BeanUtils.copyProperties(dTimbraturaFisicaRaw, this);
	}
	
}