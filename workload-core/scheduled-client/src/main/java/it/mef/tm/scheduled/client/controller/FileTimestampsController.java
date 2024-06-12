package it.mef.tm.scheduled.client.controller;


import static it.mef.tm.scheduled.client.costants.Costants.FORMAT_DATE;
import static it.mef.tm.scheduled.client.costants.Costants.SISTEMA_PROTOCOLLO_HTTP;
import static it.mef.tm.scheduled.client.costants.Costants.TYPE_API_OPERATION;
import static it.mef.tm.scheduled.client.costants.Costants.URL_BASE;
import static it.mef.tm.scheduled.client.costants.Costants.URL_SERVIZI_GFT_FILE;
import static it.mef.tm.scheduled.client.costants.Costants.URL_SERVIZI_GFT_UPLOAD_FILE;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.mef.tm.scheduled.client.exception.ErrorCode;
import it.mef.tm.scheduled.client.exception.PreconditionException;
import it.mef.tm.scheduled.client.util.StringUtility;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api(value = URL_BASE + URL_SERVIZI_GFT_FILE, protocols = SISTEMA_PROTOCOLLO_HTTP)
@RestController
@RequestMapping(URL_BASE + URL_SERVIZI_GFT_FILE)
public class FileTimestampsController {
	
	@Value("${path.timbrature}")
	private String pathTimbrature;
	
	/**
	 * Servizio per il caricamento di un file timbrature
	 * @param file
	 * @return
	 * @throws PreconditionException
	 */
	@ApiOperation(value = TYPE_API_OPERATION, notes = "Upload of Timestamps File")
	@PostMapping(value = URL_SERVIZI_GFT_UPLOAD_FILE, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Boolean uploadFile(@ApiParam(value = "File di upload", required = true) @RequestPart(name="file", required = true) MultipartFile file) throws PreconditionException {
		String pathFinale = StringUtility.concat(pathTimbrature, File.separator, new SimpleDateFormat(FORMAT_DATE).format(new Date()), "-", file.getOriginalFilename());
		try {
			Files.write(Paths.get(pathFinale), file.getBytes(), StandardOpenOption.CREATE_NEW);
		} catch(IOException ex) {
			log.error(ErrorCode.TMGFT33.getCode(), ex);
			throw new PreconditionException(ErrorCode.TMGFT33.getCode());
		}
		return true;
	}

}
