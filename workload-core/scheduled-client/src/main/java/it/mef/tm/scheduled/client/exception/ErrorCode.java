package it.mef.tm.scheduled.client.exception;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;

/**
 * ErrorCode.java
 * codici errori
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  4 apr 2024
 * @Description prima versione
 */
public enum ErrorCode {

	ERRSCH01("ERRSCH01", "Workload Already Stopped", null), // schedulazione in corso
	ERRSCH02("ERRSCH02", "Workload Already Started", null), // schedulazione già annullata o non presente
	TMGFT33("TMGFT33", "Error during writing file", null),
	UNEXPECTED("5000", "Internal Server Error", null),
	NULL_POINTER("NP-1000", "Null Pointer Exception", NullPointerException.class),
	ILLEGAL_ARGUMENT("IA-1000", "Illegal Argument Exception", IllegalArgumentException.class),
	ILLEGAL_STATE("IA-1000", "Illegal State Exception", IllegalStateException.class),
	HTTP_MESSAGENO_TREADABLE("HMT-1000", "HTTP Request Body Not Readble ", HttpMessageNotReadableException.class),
	METHOD_ARGUMENT_NOT_VALID("MAV-1000", "Method Arguments Not Valid", MethodArgumentNotValidException.class),
	IO("IOE-1000","Input/Output Exception", IOException.class),
	UNSUPPORTED_OPERATION("UNO-1000","Unsupported Operation Exception", UnsupportedOperationException.class);

	private final String code;
	private final String description;
	private final Class<?> clazz;
	private String clazzSon;

	ErrorCode(String code, String description, Class<?> clazz) {
		this.code = code;
		this.description = description;
		this.clazz = clazz;
	}

	public String getDescription() {
		return this.description;
	}

	public String getCode() {
		return this.code;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public String getClazzSon() {
		return clazzSon;
	}

	public void setClazzSon(String clazzSon) {
		this.clazzSon = clazzSon;
	}

	/**
	 * parsa la classe sul nome
	 * @param code
	 */
	public static ErrorCode parseCode(String code) {
		return Arrays.stream(ErrorCode.values())
				.filter(x -> x.getCode()
						.equals(code))
				.findFirst()
				.orElse(UNEXPECTED);
	}

	/**
	 * parsa la classe sull'istanza
	 * @param clazz
	 * @return ErrorCode
	 */
	public static <T extends Exception> ErrorCode parseClass(Class<T> clazz) {
		ErrorCode res = Arrays.stream(ErrorCode.values())
				.filter(x -> x.getClazz() != null)
				.filter(x -> x.getClazz().isAssignableFrom(clazz))
				.findFirst()
				.orElse(UNEXPECTED);

		if (res == UNEXPECTED) {
			res = Arrays.stream(ErrorCode.values())
					.filter(x -> x.getClazz() != null)
					.filter(x -> x.getClazz().isAssignableFrom(clazz.getSuperclass()))
					.findFirst()
					.orElse(UNEXPECTED);
			if (res != UNEXPECTED) {
				res.setClazzSon(clazz.getName());
			}
		}
		return res;
	}
}
