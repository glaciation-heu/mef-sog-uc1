package it.mef.tm.scheduled.client.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * TaskRunnerService.java
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  12 apr 2024
 * @Description prima versione
 */
@Service
public class TaskRunnerService {


    @Autowired
    private TaskService taskService;
    
    /**
     * Esegue il task classico di elabaorazione
     * Metodo runTask
     * @return
     */
    public Runnable runTask() {

    	return () -> taskService.executeTask();
    }

    /**
     * Esegue il task massivo di elabaorazione
     * Metodo runTask
     * @return
     */
    public Runnable runMassiveTask() {

    	return () -> taskService.executeMassiveTask();
    }
    
    /**
     * Esegue il task massivo schedulato
     * di elabaorazione
     * estraendo solo i file relativi al giorno/ora correnti
     * @return
     */
    public Runnable runMassiveScheduledTask() {

    	return () -> taskService.executeMassiveScheduledTask();
    }
}
