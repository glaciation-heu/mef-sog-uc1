package it.mef.tm.elaboration.timb.service.impl;

import io.minio.errors.*;
import it.mef.tm.elaboration.timb.exception.NotRollBackException;
import it.mef.tm.elaboration.timb.exception.PreconditionException;
import it.mef.tm.elaboration.timb.model.TimbraturaEsitoLettura;
import it.mef.tm.elaboration.timb.service.FileTimbratureService;
import it.mef.tm.elaboration.timb.service.LetturaTimbratureService;
import it.mef.tm.elaboration.timb.util.ListUtility;
import it.mef.tm.elaboration.timb.util.StatoAcquisizioneTimbratureEnum;
import it.mef.tm.elaboration.timb.xml.model.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static it.mef.tm.elaboration.timb.util.Constants.RESULT_EXTENSION;

/**
 * LetturaTimbratureServiceImpl.java
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  28 mag 2024
 * @Description prima versione
 */
@Service
@Transactional(rollbackFor = Exception.class, noRollbackFor = NotRollBackException.class)
public class LetturaTimbratureServiceImpl implements LetturaTimbratureService {

	@Autowired
	private FileTimbratureService fileTimbratureService;

	@Autowired
	private MinioServiceImpl minioService;
	
	@Value("${path.timbrature.elaborated}")
	private String pathElaborated;

	@Value("${path.timbrature.completed}")
	private String pathCompleted;
	
	@Value("${path.timbrature.discarded}")
	private String pathDiscarded;

	@Getter
    @Value(value = "${spring.kafka.listener.topic}")
	private String topic;

    @Override
	public void letturaFornitura(String pathToFile) throws JAXBException, IOException {

		Path fileInProgress = Paths.get(pathToFile);
		
		/**
		 * effettuo la ricerca
		 * */
		TimbraturaEsitoLettura esito;
		try {
			esito = fileTimbratureService.avviaCaricamentoManuale(fileInProgress);
		} catch (PreconditionException e) {
			// Gestisco errore
			esito = new TimbraturaEsitoLettura();
			esito.setError(e.getCode());
			esito.setStatoAcquisizioneFinale(StatoAcquisizioneTimbratureEnum.ACQ_TIMBRATURE_RAW_ERR);
			esito.setRawScartate(Collections.emptyList());
			esito.setTimbrAcquisite(Collections.emptyList());
		}
		
		boolean result = ListUtility.isNullOrEmpty(esito.getRawScartate()) && !ListUtility.isNullOrEmpty(esito.getTimbrAcquisite());
		
		/**
		 * valuto lo stato di acquisizione del file
		 * 
		 * */
		StatoAcquisizioneTimbratureEnum stato = !result ? StatoAcquisizioneTimbratureEnum.ACQ_TIMBRATURE_ERR
				: StatoAcquisizioneTimbratureEnum.ACQ_TIMBRATURE_OK;
		
		// --------- Da LR ---------
		/**
		 * setto l'esito
		 */
		esito.setStatoAcquisizioneFinale(esito.getStatoAcquisizioneFinale() != null ? esito.getStatoAcquisizioneFinale() : stato);
		
		result = result && esito.getStatoAcquisizioneFinale().equals(StatoAcquisizioneTimbratureEnum.ACQ_TIMBRATURE_OK);
		
		if (!result) {
			Files.move(fileInProgress, Paths.get(pathDiscarded+File.separator+topic+File.separator+fileInProgress.toFile().getName()), StandardCopyOption.REPLACE_EXISTING);
		} else {
			Files.move(fileInProgress, Paths.get(pathCompleted+File.separator+topic+File.separator+fileInProgress.toFile().getName()), StandardCopyOption.REPLACE_EXISTING);
		}
		
		writeElaboratedFile(fileInProgress, esito);
	} 

	/**
	 * Scrittura delle timbrature normalizzate su file
	 * nella cartella dei risultati
	 * Metodo writeElaboratedFile
	 * @param filePath
	 * @param esito
	 * @throws JAXBException
	 */
	private void writeElaboratedFile(Path filePath, TimbraturaEsitoLettura esito) throws JAXBException, IOException {
        DettaglioElaborazione ft = new DettaglioElaborazione(filePath.toFile().getName().replaceFirst("^[0-9]{14}-", ""));
        ft.setEsito(esito.getStatoAcquisizioneFinale().getLabelStato());
        ft.setTotalNumber(esito.getTimbrAcquisite().size() + esito.getRawScartate().size());
        ft.setAcquired(esito.getTimbrAcquisite().size());
        final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        ft.setTimbrature(esito.getTimbrAcquisite().stream()
        		.map(t -> new RowTimbratura(new TimbraturaFisica(t, sdf), new TimbraturaFisicaRaw(t.getDTimbraturaFisicaRaw()))).collect(Collectors.toList()));
        ft.setTimbraturaScarto(esito.getRawScartate().stream().map(ErroreEsitoTracciato::new).collect(Collectors.toList()));

        JAXBContext jaxbContext = JAXBContext.newInstance(ft.getClass());
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        // output pretty printed
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        File file = new File(pathElaborated + File.separator + topic + File.separator
        		+ filePath.toFile().getName().substring(0, filePath.toFile().getName().lastIndexOf(".")) + RESULT_EXTENSION);
        jaxbMarshaller.marshal(ft, file);

		if (minioService.isMinioEnabled()) {
			try {
				final SimpleDateFormat day = new SimpleDateFormat("dd/MM/yyyy");
				Map<String, String> tags = new HashMap()
				{
					{
						put("topic", topic);
						put("day", day.format(new Date()));
					}
				};
				minioService.uploadObject(file.getName(), file.getAbsolutePath(), tags);
			} catch (ServerException | InsufficientDataException | ErrorResponseException |
					 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException |
					 XmlParserException | InternalException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
