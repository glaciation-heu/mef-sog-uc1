package it.mef.tm.scheduled.client.exception;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * PreconditionException.java
 * propagazione delle eccezioni dai service ai controller che non causano 500
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  4 apr 2024
 * @Description prima versione
 */
@Data
@EqualsAndHashCode (callSuper = true)
public class PreconditionException extends Exception implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	
	private final String code;

	/**
	 * costruttore con parametri code e msg
	 * @param code
	 * @param msg
	 */
	public PreconditionException(String code, String msg) {
		super(msg);
        this.code = code;
	}
	
	/**
	 * costruttore con parametro msg
	 * @param msg
	 */
	public PreconditionException(String code) {
		super("");
        this.code = code;
	}
	/**
	 * costruttore con parametro msg e Throwable
	 * @param msg
	 * @param cause
	 */
	public PreconditionException(String msg, Throwable cause){
        super(msg, cause);
        this.code = null;
	}	
	
	/**
	 * costruttore con parametro code, msg e Throwable
	 * @param code
	 * @param msg
	 * @param cause
	 */
	public PreconditionException(String code, String msg, Throwable cause){
        super(msg, cause);
        this.code = code;
	}
	
}
