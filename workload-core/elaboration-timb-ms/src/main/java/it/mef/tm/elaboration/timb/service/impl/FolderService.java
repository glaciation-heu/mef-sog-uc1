package it.mef.tm.elaboration.timb.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * FolderService.java
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  06 giu 2024
 * @Description prima versione
 */
@Component
public class FolderService {


	@Value("${path.timbrature.elaborated}")
	private String pathElaborated;

	@Value("${path.timbrature.completed}")
	private String pathCompleted;
	
	@Value("${path.timbrature.discarded}")
	private String pathDiscarded;

	@Value("${path.metrics}")
	private String pathMetrics;

	@Value(value = "${spring.kafka.listener.topic}")
	private String topic;
	
	@EventListener(ApplicationReadyEvent.class)
    public void executeTask() throws IOException {

    	Files.createDirectories(Paths.get(pathElaborated));
		Files.createDirectories(Paths.get(pathElaborated, File.separator, topic));
    	Files.createDirectories(Paths.get(pathCompleted));
		Files.createDirectories(Paths.get(pathCompleted, File.separator, topic));
    	Files.createDirectories(Paths.get(pathDiscarded));
		Files.createDirectories(Paths.get(pathDiscarded, File.separator, topic));
    	Files.createDirectories(Paths.get(pathMetrics));
	}

}
