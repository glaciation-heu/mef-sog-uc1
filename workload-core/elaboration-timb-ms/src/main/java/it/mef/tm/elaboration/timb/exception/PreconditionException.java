package it.mef.tm.elaboration.timb.exception;

import lombok.Getter;

/**
 * PreconditionException.java
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  22 mag 2024
 * @Description prima versione
 */
public class PreconditionException extends Exception {

	@Getter
	private final String code;
	
	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	/**
	 * costruttore con parametro message e Throwable
	 * @param message
	 * @param cause
	 */
	public PreconditionException(String code, String message, Throwable cause){
		super(message, cause);
        this.code = code;
	}
	/**
	 * costruttore con parametro message e Throwable
	 * @param message
	 * @param cause
	 */
	public PreconditionException(String message, Throwable cause){
		super(message, cause);
        this.code = message;
	}
	/**
	 * costruttore con parametro Throwable
	 * @param cause
	 */
	public PreconditionException(Throwable cause) {
		super(cause);
		this.code = "";
	}
	/**
	 * costruttore con parametro message
	 * @param message
	 */
	public PreconditionException(String message) {
		super(message);
        this.code = message;
    }
}
