package it.mef.tm.scheduled.client.controller;


import static it.mef.tm.scheduled.client.costants.Costants.SISTEMA_PROTOCOLLO_HTTP;
import static it.mef.tm.scheduled.client.costants.Costants.TYPE_API_OPERATION;
import static it.mef.tm.scheduled.client.costants.Costants.URL_BASE;
import static it.mef.tm.scheduled.client.costants.Costants.URL_SERVIZI_GFT_JOB;
import static it.mef.tm.scheduled.client.costants.Costants.URL_SERVIZI_GFT_MASS_START;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.mef.tm.scheduled.client.exception.ErrorCode;
import it.mef.tm.scheduled.client.exception.PreconditionException;
import it.mef.tm.scheduled.client.service.TaskRunnerService;
import it.mef.tm.scheduled.client.util.ScheduledUtil;
import it.mef.tm.scheduled.client.util.StringUtility;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api(value = URL_BASE + URL_SERVIZI_GFT_JOB, protocols = SISTEMA_PROTOCOLLO_HTTP)
@RestController
@RequestMapping(URL_BASE + URL_SERVIZI_GFT_JOB)
public class MassiveWorkloadController {

    @Autowired
    private TaskRunnerService taskRunnerService;
	
    @Autowired
    private TaskScheduler taskScheduler;
    
	@Value("${path.timbrature}")
	private String pathTimbrature;

	/**
	 * Servizio per lo start del processo di watching 
	 * della cartella contenente le timbrature da acquisire
	 * @return
	 * @throws PreconditionException
	 */
	@ApiOperation(value = TYPE_API_OPERATION, notes = "Service starts loop of massive workload of collecting timestamps")
	@PostMapping(value = URL_SERVIZI_GFT_MASS_START)
	public Boolean startWorkloadLoop(@ApiParam(value = "Rate for loop in minutes", required = true) @RequestParam(name = "loop-rate", required = true, defaultValue = "5") int loopRate,
			@ApiParam(value = "File di upload", required = true) @RequestPart(name="zip-file", required = true) MultipartFile fileZip) throws PreconditionException {
		// Se c'è già una schedulazione  
		// e non è stata già annullata
		// Lancio un'eccezione
		if (ScheduledUtil.isScheduledFutureActive()) {
			throw new PreconditionException(ErrorCode.ERRSCH02.getCode());
		}

		String pathFinale = StringUtility.concat(pathTimbrature, File.separator, "massive", File.separator, fileZip.getOriginalFilename());
		
		try {
			Files.write(Paths.get(pathFinale), fileZip.getBytes(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
		} catch(IOException ex) {
			log.error(ErrorCode.TMGFT33.getCode(), ex);
			throw new PreconditionException(ErrorCode.TMGFT33.getCode());
		}
		
		ScheduledFuture<?> scheduledFuture = taskScheduler.scheduleAtFixedRate(taskRunnerService.runMassiveTask(), loopRate * 1000 * 60);
		ScheduledUtil.storeScheduledFuture(scheduledFuture);
		return true;
	}

}
