package it.mef.tm.elaboration.timb.exception;

import org.springframework.kafka.KafkaException;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessServiceException extends KafkaException {

	/** serialVersionUID */
	private static final long serialVersionUID = 5110330616228121793L;
	private final String code;

	public BusinessServiceException(String code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}
	
	public BusinessServiceException(String code, String message) {
		super(message);
		this.code = code;
	}
}
