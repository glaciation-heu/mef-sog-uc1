package it.mef.tm.scheduled.client.service;

import static it.mef.tm.scheduled.client.costants.Costants.FORMAT_DATE;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.archivers.zip.ZipFile.Builder;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.mef.tm.scheduled.client.costants.GiorniSettimana;
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

	@Value("${path.timbrature.completed}")
	private String pathTimbratureCompleted;

	@Value("${path.timbrature.discarded}")
	private String pathTimbratureDiscarded;

	/**
	 * Servizio che esegue l'elaborazione massiva del task
	 * in modo schedulato. <br>
	 * Si aspetta che il file zip sia strutturato nel seguente modo: <br><br>
	 * <b>zipFile.zip</b> <br>&emsp;
	 *    - GIORNO DELLA SETTIMANA espresso come girno da 1 a 7, 
	 *                           oppure in formato di almeno 3 lettere (LUN/MAR/.../DOM o MON/TUE/...SUN) <br>&emsp;&emsp;
	 *       - ORA DEL GIORNO/FASCIA ORARIA espressa in numeri da 0 a 23 (es. 9 oppure 9-13) <br>&emsp;&emsp;&emsp;
	 *          - tracciato.xml <br>
	 */
    public void executeMassiveScheduledTask() {
		log.info("Process copying files started");
		File[] listFiles = Paths.get(pathTimbrature + File.separator + "massive").toFile().listFiles();
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE);
    	for (File file : listFiles) {
    		try {
    			createMassiveFiles(file, sdf, true);
    		} catch (Exception e) {
    			log.error(e.getMessage(), e);
    		}
		}
    	log.info("Process copying files end");
		
		executeTask();
	}

	/**
	 * Servizio che esegue l'elaborazione massiva del task.
	 * Vengono presi tutti i file contenuti nello zip 
	 * e depositati sotto la cartella delle timbrature da acquisire.
	 */
    public void executeMassiveTask() {
		log.info("Process copying files started");
		File[] listFiles = Paths.get(pathTimbrature + File.separator + "massive").toFile().listFiles();
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE);
    	for (File file : listFiles) {
    		try {
    			createMassiveFiles(file, sdf, false);
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
	 * @param sdf
	 * @param verifyTime
     * @throws IOException 
     */
    private void createMassiveFiles(File fileZip, SimpleDateFormat sdf, boolean verifyTime) throws IOException {
        
		try (ZipFile zFile = new Builder().setFile(fileZip).get()) {
	    	Enumeration<ZipArchiveEntry> entries = zFile.getEntries();
	    	while (entries.hasMoreElements()) {
	    		createFile(zFile, entries.nextElement(), sdf, verifyTime);
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
	 * @param verifyTime
	 * @return
	 * @throws IOException
	 */
	private void createFile(ZipFile zFile, ZipArchiveEntry zipEntry, SimpleDateFormat sdf, boolean verifyTime) throws IOException {
		if (!zipEntry.isDirectory()) {
			File fileEntry = new File(zipEntry.getName());
			if (!verifyTime || verifyScheduling(fileEntry)) {
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
	}
    
	/**
	 * Verifica che la data attuale 
	 * corrisponda ad un giorno della settimana + orario presente nell'entry.
	 * L'entry deve essere così strutturata:
	 * parent(day)/parent(hour)/file.xml
	 * @param fileEntry
	 * @return
	 */
	private boolean verifyScheduling(File fileEntry) {
		// Se l'entry risulta mancante della cartella "padre"
		// e della cartella "nonno", non è possibile effettuare il controllo
		if (fileEntry.getParentFile() == null || fileEntry.getParentFile().getParentFile() == null) {
			return false;
		}
		
		DateTime dt = new DateTime();  // current time
		int dayOfWeek = dt.getDayOfWeek();     // gets the day of week
		String time = fileEntry.getParentFile().getName();
		String[] split = time.split("-");
		int start = Integer.parseInt(split[0]);
		int end = start;
		if (split.length > 1) {
			end = Integer.parseInt(split[1]);
		}
		boolean verifyTime = IntStream.rangeClosed(start, end).anyMatch(i -> i == dt.getHourOfDay());

		String day = fileEntry.getParentFile().getParentFile().getName();
		
		boolean verifyDay = GiorniSettimana.verifyDay(day, dayOfWeek);
		
		return verifyDay && verifyTime;
	}
	
	/**
	 * Metodo executeTask
	 */
    public void executeTask() {
		log.info("Process started");
		deleteOldFileCompleted();
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

	/**
	 * Questo metodo elimina i file completati più vecchi di un mese.
	 * In questo modo si evita la saturazione del volume
	 */
	public void deleteOldFileCompleted() {

		File directoryCompleted = new File(pathTimbratureCompleted);
		File directoryDiscarded = new File(pathTimbratureDiscarded);

		FileFilter filter = file -> {
            if (!file.isFile()) return false;
            LocalDateTime now = new LocalDateTime();
            LocalDateTime dt = new LocalDateTime(file.lastModified());
            if (dt!=null && dt.toDateTime().isBefore(now.minusMonths(1).toDateTime()))
                return true;
            return false;
        };

		File[] fileToDeleteCompleted = directoryCompleted.listFiles(filter);
		File[] fileToDeleteDiscarded = directoryDiscarded.listFiles(filter);

		if (fileToDeleteCompleted != null) {
			for(File f: fileToDeleteCompleted) {
				f.delete();
			}
		}
		if (fileToDeleteDiscarded != null) {
			for(File f: fileToDeleteDiscarded) {
				f.delete();
			}
		}

	}
}
