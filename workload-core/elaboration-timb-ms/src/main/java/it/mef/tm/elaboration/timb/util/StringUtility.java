package it.mef.tm.elaboration.timb.util;

import java.util.Arrays;
import java.util.Objects;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * StringUtility.java
 * classe di util per le stringhe
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  4 apr 2024
 * @Description prima versione
 */
@NoArgsConstructor(access = AccessLevel.NONE)
public class StringUtility {
	
	/**
	 * Metodo utilizzato per la concatenazione delle stringhe, 
	 * va utilizzato per superare l'analisi CAST
	 * @param args
	 * @return
	 */
	public static String concat(String... args) {
		StringBuilder stringBuilder = new StringBuilder();
		Arrays.stream(args).filter(Objects::nonNull).forEach(stringBuilder::append);
		return stringBuilder.toString();
	}

	/**
	 * Metodo che verifica se la stringa in input
	 * sia non valorizzata oppure vuota
	 * @param s
	 * @return
	 */
	public static boolean isNullOrEmpty(String s) {
		return s == null || s.isEmpty();
	}
}
