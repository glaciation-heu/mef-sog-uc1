package it.mef.tm.scheduled.client.controller;


import static it.mef.tm.scheduled.client.costants.Costants.SISTEMA_PROTOCOLLO_HTTP;
import static it.mef.tm.scheduled.client.costants.Costants.TYPE_API_OPERATION;
import static it.mef.tm.scheduled.client.costants.Costants.URL_BASE;
import static it.mef.tm.scheduled.client.costants.Costants.URL_SERVIZI_GFT_JOB;
import static it.mef.tm.scheduled.client.costants.Costants.URL_SERVIZI_GFT_MASS_SCHED_START;
import static it.mef.tm.scheduled.client.costants.Costants.URL_SERVIZI_GFT_MASS_START;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ScheduledFuture;
import java.util.zip.ZipInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
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
	public Boolean startWorkloadLoop(
			@ApiParam(value = "Hour of loop process, ex.: \"9,18\" (from 9AM to 9:59AM and from 6PM to 6:59PM) or \"8-10,17-19\" (from 8AM to 9:59AM and from 5PM to 6:59PM)", example = "8-10,17-19") @RequestParam(name = "hour", required = false) String hour,
			@ApiParam(value = "Days of loop process, ex.: \"MON-FRI\" (from Monday to Friday) or \"MON,WED,FRI\" (Monday, Wednesday and Friday)", example = "MON-FRI") @RequestParam(name = "days", required = false) String days,
			@ApiParam(value = "Rate for loop in minutes", required = true) @RequestParam(name = "loop-rate", required = true, defaultValue = "5") int loopRate,
			@ApiParam(value = "File to upload (only .zip file)", required = true) @RequestPart(name="zip-file", required = true) MultipartFile fileZip) throws PreconditionException {
		// Se c'è già una schedulazione  
		// e non è stata già annullata
		// Lancio un'eccezione
		if (ScheduledUtil.isScheduledFutureActive()) {
			throw new PreconditionException(ErrorCode.ERRSCH02.getCode());
		}
		
		try (ZipInputStream zis = new ZipInputStream(fileZip.getInputStream())) {
			if (zis.getNextEntry() == null) {
				zis.closeEntry();
				throw new PreconditionException(ErrorCode.ERRSCH03.getCode());
			}
			zis.closeEntry();
		} catch (IOException e) {
			log.error(ErrorCode.ERRSCH03.getCode(), e);
			throw new PreconditionException(ErrorCode.ERRSCH03.getCode());
		} 
		
		String pathFinale = StringUtility.concat(pathTimbrature, File.separator, "massive", File.separator, fileZip.getOriginalFilename());
		
		try {
			cleanFolder(StringUtility.concat(pathTimbrature, File.separator, "massive"));
			Files.write(Paths.get(pathFinale), fileZip.getBytes(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
		} catch(IOException ex) {
			log.error(ErrorCode.TMGFT33.getCode(), ex);
			throw new PreconditionException(ErrorCode.TMGFT33.getCode());
		}
		
		String cronExpr = String.format("0 */%s %s * * %s", loopRate, StringUtility.isNullOrEmpty(hour) ? "*" : hour, StringUtility.isNullOrEmpty(days) ? "*" : days);
		
		ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(taskRunnerService.runMassiveTask(), new CronTrigger(cronExpr));
		
		log.info("Scheduled cron task: {}", cronExpr);
		
		ScheduledUtil.storeScheduledFuture(scheduledFuture);
		return true;
	}


	/**
	 * Metodo cleanFolder
	 * @param listFiles
	 * @throws IOException
	 */
	private static void cleanFolder(String path) throws IOException {
		File[] listFiles = Paths.get(path).toFile().listFiles();
		for (File file : listFiles) {
			if (file.isFile()) {
				Files.delete(file.toPath());
			}
		}
	}
	

	/**
	 * Servizio per lo start del processo di watching 
	 * della cartella contenente le timbrature da acquisire
	 * @return
	 * @throws PreconditionException
	 */
	@ApiOperation(value = TYPE_API_OPERATION, notes = "Service starts massive workload of collecting time stamps based on the day of week and the time of execution."
			+ "Upload a zip file with the following structure: \n"
			+ "<i>zip-file.zip</i>: \n"
			+ "&emsp; - <i>DAY OF WEEK</i> (sub-folder pattern accepted: 1 to 7 or minumum of 3 letters of day 'MON','TUE', etc.) \n"
			+ "&emsp;&emsp; - <i>HOUR OF DAY</i> (sub-folder pattern accepted: numbers from 0 to 23, or range of hours like 9-13) \n"
			+ "&emsp;&emsp;&emsp; - <i>timestamps-file.xml</i> \n"
			+ "This structure allows to set which time stamps files must be elaborated for certain hours of certain days of the week.")
	@PostMapping(value = URL_SERVIZI_GFT_MASS_SCHED_START)
	public Boolean startWorkloadScheduled(
			@ApiParam(value = "Minute of the hour when the elaboration starts. If not set, the scheduling is done every hour at 00 (Ex. 9:00AM, 10:00AM, etc.)") @RequestParam(name = "minute", required = false) Integer minute,
			@ApiParam(value = "File to upload (only .zip file)", required = true) @RequestPart(name="zip-file", required = true) MultipartFile fileZip) throws PreconditionException {
		// Se c'è già una schedulazione  
		// e non è stata già annullata
		// Lancio un'eccezione
		if (ScheduledUtil.isScheduledFutureActive()) {
			throw new PreconditionException(ErrorCode.ERRSCH02.getCode());
		}
		
		try (ZipInputStream zis = new ZipInputStream(fileZip.getInputStream())) {
			if (zis.getNextEntry() == null) {
				zis.closeEntry();
				throw new PreconditionException(ErrorCode.ERRSCH03.getCode());
			}
			zis.closeEntry();
		} catch (IOException e) {
			log.error(ErrorCode.ERRSCH03.getCode(), e);
			throw new PreconditionException(ErrorCode.ERRSCH03.getCode());
		} 
		
		String pathFinale = StringUtility.concat(pathTimbrature, File.separator, "massive", File.separator, fileZip.getOriginalFilename());
		
		try {
			cleanFolder(StringUtility.concat(pathTimbrature, File.separator, "massive"));
			Files.write(Paths.get(pathFinale), fileZip.getBytes(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
		} catch(IOException ex) {
			log.error(ErrorCode.TMGFT33.getCode(), ex);
			throw new PreconditionException(ErrorCode.TMGFT33.getCode());
		}
		
		String cronExpression = minute != null ? "0 " + minute + " * * * *" : "0 0 * * * *";
		
		ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(taskRunnerService.runMassiveScheduledTask(), new CronTrigger(cronExpression));
		
		log.info("Scheduled cron task: {}", cronExpression);
		
		ScheduledUtil.storeScheduledFuture(scheduledFuture);
		return true;
	}


}
