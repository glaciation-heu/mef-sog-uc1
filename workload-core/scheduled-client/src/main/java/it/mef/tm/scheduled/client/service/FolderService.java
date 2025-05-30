package it.mef.tm.scheduled.client.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import it.mef.tm.scheduled.client.util.StringUtility;

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


	@Value("${path.timbrature}")
	private String pathTimbrature;

	@Value("${path.timbrature.to-be-elaborated}")
	private String pathTimbratureToBeElaborated;

	@Value("${spring.kafka.producer.topic}")
	private String topicDefault;

	@Value("${spring.kafka.producer.topic-extra}")
	private String topicExtra;

	@EventListener(ApplicationReadyEvent.class)
    public void executeTask() throws IOException {

    	Files.createDirectories(Paths.get(pathTimbrature));
    	Files.createDirectories(Paths.get(pathTimbratureToBeElaborated));
    	Files.createDirectories(Paths.get(pathTimbratureToBeElaborated, File.separator, topicDefault));
    	Files.createDirectories(Paths.get(pathTimbratureToBeElaborated, File.separator, topicExtra));
    	Files.createDirectories(Paths.get(StringUtility.concat(pathTimbrature, File.separator, "massive")));
	}

}
