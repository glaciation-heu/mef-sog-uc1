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
    
    public Runnable runTask() {

    	return () -> taskService.executeTask();
    }
}
