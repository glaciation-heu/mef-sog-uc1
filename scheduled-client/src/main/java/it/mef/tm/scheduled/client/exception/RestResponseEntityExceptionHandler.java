package it.mef.tm.scheduled.client.exception;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * RestResponseEntityExceptionHandler.java
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  19 apr 2024
 * @Description prima versione
 */
@Slf4j
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		log.error(ex.getMessage(), ex);
		ErrorCode er = ErrorCode.parseClass(ex.getClass());
		FaultResponse response = new FaultResponse(er, ex.getMessage(), ex.getBindingResult().getFieldErrors());
		return handleExceptionInternal(ex, response, headers, HttpStatus.BAD_REQUEST, request);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(final HttpMessageNotReadableException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		log.error(ex.getMessage(), ex);
		return handleExceptionInternal(ex, buildResponse(ex), headers, HttpStatus.BAD_REQUEST, request);
	}

	/**
	 * intercetta le eccezioni che danno 500}
	 * @param ex
	 * @param request
	 */
	// 500
	@ExceptionHandler({
			NullPointerException.class,
			IllegalArgumentException.class,
			IllegalStateException.class,
			UnsupportedOperationException.class,
			IOException.class,
			Exception.class})
	public ResponseEntity<FaultResponse> handleInternal(final Exception ex, final WebRequest request) {
		log.error(ex.getMessage(), ex);

		return new ResponseEntity<>(buildResponse(ex), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * intercetta le eccezioni custom che non danno 500}
	 * @param ex
	 * @param request
	 */
	@ExceptionHandler({
		PreconditionException.class
	})
	public ResponseEntity<FaultResponse> handleCustomConditions(final PreconditionException ex, final WebRequest request) {
		log.error(ex.getMessage(), ex);
		ErrorCode error = ErrorCode.parseCode(ex.getCode());
		return new ResponseEntity<>(new FaultResponse(error.getCode(), error, error.getDescription()), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Costrutisce la risposta senza lingua}
	 * @param ex
	 */
	private FaultResponse buildResponse(Exception ex) {
		ErrorCode er = ErrorCode.parseClass(ex.getClass());
		return new FaultResponse(er, ex.getMessage());
	}
}
