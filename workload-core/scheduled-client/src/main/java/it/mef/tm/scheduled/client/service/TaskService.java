package it.mef.tm.scheduled.client.service;

import static it.mef.tm.scheduled.client.costants.Costants.FORMAT_DATE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.mef.tm.scheduled.client.kafka.KafkaProducerService;
import it.mef.tm.scheduled.client.model.ElaborazioneModel;
import it.mef.tm.scheduled.client.util.StringUtility;
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

    public void executeMassiveTask() {
		log.info("Process copying files started");
		File[] listFiles = Paths.get(pathTimbrature + File.separator + "massive").toFile().listFiles();
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE);
    	for (File file : listFiles) {
    		try {
    			createMassiveFiles(file, sdf);
    		} catch (Exception e) {
    			log.error(e.getMessage(), e);
    		}
		}
    	log.info("Process copying files end");
		
		executeTask();
	}

    /**
     * Metodo createMassiveFiles
     * @param fileZip
     * @throws IOException 
     */
    private void createMassiveFiles(File fileZip, SimpleDateFormat sdf) throws IOException {
        
		try (ZipFile zFile = new ZipFile(fileZip)) {
	    	Enumeration<? extends ZipEntry> entries = zFile.entries();
	    	while (entries.hasMoreElements()) {
	    		createFile(zFile, entries.nextElement(), sdf);
	    	}

		} catch (IOException e) {
			log.error("Error opening file " + fileZip.getName());
			// Rilancio l'eccezione unchecked dato 
			// che non è possibile modificare la firma del metodo run
			throw e;
		}
    }

	/**
	 * Metodo createFile
	 * @param zFile
	 * @param zipEntry
	 * @param sdf
	 * @return
	 * @throws IOException
	 */
	private void createFile(ZipFile zFile, ZipEntry zipEntry, SimpleDateFormat sdf) throws IOException {
		if (!zipEntry.isDirectory()) {
    		try {
    			Path path = Paths.get(StringUtility.concat(pathTimbrature, File.separator, sdf.format(new Date()), "-", new File(zipEntry.getName()).getName()));
				Files.copy(zFile.getInputStream(zipEntry), path);
			} catch (IOException e) {
				log.error("Error copying file " + zipEntry.getName());
				// Rilancio l'eccezione unchecked dato 
				// che non è possibile modificare la firma del metodo run
				throw e;
			}
		}
	}
    
	/**
	 * Metodo executeTask
	 */
    public void executeTask() {
		log.info("Process started");
    	File[] listFiles = Paths.get(pathTimbrature).toFile().listFiles();
    	for (File file : listFiles) {
    		try {
    			moveFileWithKafka(file);
    		} catch (Exception e) {
    			log.error(e.getMessage(), e);
    		}
		}
    	log.info("Process end");
	}

	/**
	 * Metodo moveFileWithKafka
	 * @param file
	 * @throws IOException 
	 * @throws Exception
	 */
	private void moveFileWithKafka(File file) throws IOException {
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
				throw e;
			}
			try {
				kafkaProducerService.publishFlussoElaborazione(elabModel);
			} catch (Exception e) {
				log.error("Error sending Kafka Message ", e);
				// Riporto il file nella cartella originale
				try {
					Files.move(move, Paths.get(StringUtility.concat(file.toPath().getParent().toString(), File.separator, file.getName())));
				} catch (IOException e1) {
					log.error("Error moving file " + file.getName());
					// Rilancio l'eccezione unchecked dato 
					// che non è possibile modificare la firma del metodo run
					throw e1;
				}
				
				throw e;
			}
		}
	}
}
