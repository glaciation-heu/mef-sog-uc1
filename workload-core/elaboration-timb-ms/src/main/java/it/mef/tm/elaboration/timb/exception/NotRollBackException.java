package it.mef.tm.elaboration.timb.exception;

import lombok.Getter;

/**
 * NotRollBackException.java
 * propagazione delle eccezioni dai service ai controller che non causano rollback
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  23 mag 2024
 * @Description prima versione
 */
public class NotRollBackException extends Exception {

	@Getter
	private final String code; 
	
	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	/**
	 * costruttore con parametro message e Throwable
	 * @param message
	 * @param cause
	 */
	public NotRollBackException(String code, String message, Throwable cause){
		super(message, cause);
        this.code = code;
	}
	/**
	 * costruttore con parametro message e Throwable
	 * @param message
	 * @param cause
	 */
	public NotRollBackException(String message, Throwable cause){
		super(message, cause);
        this.code = "";
	}
	/**
	 * costruttore con parametro Throwable
	 * @param cause
	 */
	public NotRollBackException(Throwable cause) {
		super(cause);
        this.code = "";
	}
	/**
	 * costruttore con parametro Throwable
	 * @param cause
	 */
	public NotRollBackException(PreconditionException cause) {
		super(cause);
        this.code = cause.getCode();
	}
	/**
	 * costruttore con parametro message
	 * @param message
	 */
	public NotRollBackException(String message) {
		super(message);
        this.code = message;
    }
}
