package it.mef.tm.scheduled.client.service;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.mef.tm.scheduled.client.kafka.KafkaProducerService;
import it.mef.tm.scheduled.client.util.StringUtility;
import it.mef.tm.scheduled.client.util.model.ElaborazioneModel;
import lombok.extern.slf4j.Slf4j;

/**
 * TaskRunnerService.java
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  12 apr 2024
 * @Description prima versione
 */
@Service
@Slf4j
public class TaskService {

	@Autowired
    private KafkaProducerService kafkaProducerService;

	@Value("${path.timbrature}")
	private String pathTimbrature;

	@Value("${path.timbrature.to-be-elaborated}")
	private String pathTimbratureToBeElaborated;
    
    public void executeTask() {
		log.info("Process started");
    	File[] listFiles = Paths.get(pathTimbrature).toFile().listFiles();
    	for (File file : listFiles) {
			moveFileWithKafka(file);
		}
    	log.info("Process end");
	}

	/**
	 * Metodo moveFileWithKafka
	 * @param file
	 * @throws Exception
	 */
	private void moveFileWithKafka(File file) {
		if (file.isFile()) {
			ElaborazioneModel elabModel = new ElaborazioneModel();
			// Sposto il file
			Path move = null;
			try {
				// Sposto il file
				move = Files.move(file.toPath(), 
						Paths.get(StringUtility.concat(pathTimbratureToBeElaborated, File.separator, file.getName())));
				elabModel.setPathToFile(move.toString());
			} catch (IOException e) {
				log.error("Error moving file " + file.getName());
				// Rilancio l'eccezione unchecked dato 
				// che non è possibile modificare la firma del metodo run
				throw new UncheckedIOException(e);
			}
			try {
				kafkaProducerService.publishFlussoElaborazione(elabModel);
			} catch (Exception e) {
				log.error("Error sending Kafka Message ", e);
				// Riporto il file nella cartella originale
				try {
					Files.move(move, file.toPath().getParent());
				} catch (IOException e1) {
					log.error("Error moving file " + file.getName());
					// Rilancio l'eccezione unchecked dato 
					// che non è possibile modificare la firma del metodo run
					throw new UncheckedIOException(e1);
				}
				
				throw e;
			}
		}
	}
}
