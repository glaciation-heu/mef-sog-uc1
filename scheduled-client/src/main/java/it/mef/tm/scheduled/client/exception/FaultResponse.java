package it.mef.tm.scheduled.client.exception;

import java.util.Date;
import java.util.List;

import org.springframework.validation.FieldError;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NonNull;

/**
 * FaultResponse.java
 * risposte con errore
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  4 apr 2024
 * @Description prima versione
 */
@Getter
public class FaultResponse {

	@NonNull
	@JsonProperty("code")
	private String errorCode;

	@NonNull
	@JsonProperty("description")
	private String errorDescription;

	@JsonProperty("type_class_error")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String classOfError;

	private Date timestamp;

	@JsonProperty("message")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private  String errorMessage;

	@JsonProperty("method_parameter_field")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<FieldError> fieldError;


	FaultResponse(ErrorCode errorCode) {
		this.errorCode = errorCode.getCode();
		this.errorDescription = errorCode.getDescription();
		this.timestamp = new Date();
		this.classOfError = errorCode.getClazzSon();
	}

	FaultResponse(ErrorCode errorCode, String errorMessage) {
		this(errorCode);
		this.errorMessage = errorMessage;
	}
	
	FaultResponse(String code, ErrorCode errorCode, String errorMessage) {
		this(errorCode);
		this.errorCode = code;
		this.errorMessage = errorMessage;
	}

	/**
	 * Costruttore dedicato all'errore di tipo: MethodArgumentNotValidException}
	 * @param errorCode
	 * @param errorMessage
	 * @param fieldError
	 */
	FaultResponse(ErrorCode errorCode, String errorMessage, List<FieldError> fieldError) {
		this(errorCode, errorMessage);
		this.fieldError = fieldError;
	}

}
