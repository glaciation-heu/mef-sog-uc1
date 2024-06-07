package it.mef.tm.scheduled.client.controller;


import static it.mef.tm.scheduled.client.costants.Costants.SISTEMA_PROTOCOLLO_HTTP;
import static it.mef.tm.scheduled.client.costants.Costants.TYPE_API_OPERATION;
import static it.mef.tm.scheduled.client.costants.Costants.URL_BASE;
import static it.mef.tm.scheduled.client.costants.Costants.URL_SERVIZI_GFT_JOB;
import static it.mef.tm.scheduled.client.costants.Costants.URL_SERVIZI_GFT_START;
import static it.mef.tm.scheduled.client.costants.Costants.URL_SERVIZI_GFT_STOP;

import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import it.mef.tm.scheduled.client.exception.ErrorCode;
import it.mef.tm.scheduled.client.exception.PreconditionException;
import it.mef.tm.scheduled.client.service.TaskRunnerService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api(value = URL_BASE + URL_SERVIZI_GFT_JOB, protocols = SISTEMA_PROTOCOLLO_HTTP)
@RestController
@RequestMapping(URL_BASE + URL_SERVIZI_GFT_JOB)
public class WorkloadController {

    @Autowired
    private TaskRunnerService taskRunnerService;
	
    @Autowired
    private TaskScheduler taskScheduler;
    
    private ScheduledFuture<?> scheduledFuture;
    
	@Value("${fixedRate.watching.folder.seconds}")
	private Long rate;

	/**
	 * Servizio per lo start del processo di watching 
	 * della cartella contenente le timbrature da acquisire
	 * @return
	 * @throws PreconditionException
	 */
	@ApiOperation(value = TYPE_API_OPERATION, notes = "Service starts the workload of collecting timestamps")
	@PostMapping(value = URL_SERVIZI_GFT_START)
	public Boolean startWorkload() throws PreconditionException {
		// Se c'è già una schedulazione 
		// e non è stata già annullata
		// Lancio un'eccezione
		if (isScheduledFutureActive()) {
			throw new PreconditionException(ErrorCode.ERRSCH02.getCode());
		}
		scheduledFuture = taskScheduler.scheduleAtFixedRate(taskRunnerService.runTask(), rate * 1000);
		return true;
	}

	/**
	 * Servizio per lo start del processo di watching 
	 * della cartella contenente le timbrature da acquisire
	 * @return
	 * @throws PreconditionException
	 */
	@ApiOperation(value = TYPE_API_OPERATION, notes = "Service stops the workload of collecting timestamps")
	@PostMapping(value = URL_SERVIZI_GFT_STOP)
	public Boolean stopWorkload() throws PreconditionException {
		// Se non c'è ancora una schedulazione 
		// o è stata già annullata
		// Lancio un'eccezione
		if (!isScheduledFutureActive()) {
			throw new PreconditionException(ErrorCode.ERRSCH01.getCode());
		}
		scheduledFuture.cancel(true);
		log.info("Schedulazione interrotta: {}", scheduledFuture.isCancelled());
		return scheduledFuture.isCancelled();
	}
	
	/**
	 * Verifica che la schedulazione sia attiva
	 * verificando scheduledFuture <> null AND scheduledFuture.isCancelled <> true
	 * AND scheduledFuture.isDone <> true
	 * @return
	 */
	private boolean isScheduledFutureActive() {
		return scheduledFuture != null && !scheduledFuture.isCancelled() && !scheduledFuture.isDone();
	}
}
